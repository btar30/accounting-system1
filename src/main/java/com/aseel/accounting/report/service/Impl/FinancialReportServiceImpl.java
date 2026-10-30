package com.aseel.accounting.report.service.Impl;


import com.aseel.accounting.account.model.Account;
import com.aseel.accounting.account.model.Enum.AccountType;
import com.aseel.accounting.account.repository.AccountRepository;
import com.aseel.accounting.journal.model.Enum.JournalEntryStatus;
import com.aseel.accounting.journal.model.JournalEntryLine;
import com.aseel.accounting.journal.repository.JournalEntryLineRepository;
// تأكد من استيراد كلاس السطر الخاص بالقيود هنا، مثال:
// import com.aseel.accounting.journal.model.JournalEntryLine;

import com.aseel.accounting.report.dto.IncomeStatementItemDto;
import com.aseel.accounting.report.dto.IncomeStatementResponseDto;
import com.aseel.accounting.report.dto.TrialBalanceItemDto;
import com.aseel.accounting.report.dto.TrialBalanceResponseDto;
import com.aseel.accounting.report.service.FinancialReportService;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FinancialReportServiceImpl implements FinancialReportService {

    private final AccountRepository accountRepository;
    private final JournalEntryLineRepository journalEntryLineRepository;

    @Override
    public TrialBalanceResponseDto getTrialBalance(String companyBusinessCode) {
        // 1. جلب كافة حسابات الشركة
        List<Account> accounts = accountRepository.findByCompanyBusinessCode(companyBusinessCode);

        List<TrialBalanceItemDto> items = new ArrayList<>();
        BigDecimal grandTotalDebit = BigDecimal.ZERO;
        BigDecimal grandTotalCredit = BigDecimal.ZERO;

        // 2. المرور على كل حساب لحساب إجمالي المدين والدائن من القيود المرحّلة (POSTED)
        for (Account account : accounts) {
            BigDecimal totalDebit = journalEntryLineRepository.sumDebitByAccountIdAndStatus(
                    account.getId(), JournalEntryStatus.POSTED);

            BigDecimal totalCredit = journalEntryLineRepository.sumCreditByAccountIdAndStatus(
                    account.getId(), JournalEntryStatus.POSTED);

            // حساب الرصيد الصافي (مدين - دائن)
            BigDecimal netBalance = totalDebit.subtract(totalCredit);

            // بناء عنصر التقرير لهذا الحساب
            TrialBalanceItemDto item = TrialBalanceItemDto.builder()
                    .accountId(account.getId())
                    .accountCode(account.getAccountCode())
                    .accountNameAr(account.getNameAr())
                    .accountNameEn(account.getNameEn())
                    .accountType(account.getAccountType())
                    .totalDebit(totalDebit)
                    .totalCredit(totalCredit)
                    .netBalance(netBalance)
                    .build();

            items.add(item);

            // تجميع الإجماليات العامة للميزان
            grandTotalDebit = grandTotalDebit.add(totalDebit);
            grandTotalCredit = grandTotalCredit.add(totalCredit);
        }

        // 3. التحقق من تطابق الميزان
        boolean isBalanced = grandTotalDebit.compareTo(grandTotalCredit) == 0;

        // 4. بناء الرد النهائي
        return TrialBalanceResponseDto.builder()
                .companyBusinessCode(companyBusinessCode)
                .reportDate(LocalDate.now())
                .items(items)
                .grandTotalDebit(grandTotalDebit)
                .grandTotalCredit(grandTotalCredit)
                .isBalanced(isBalanced)
                .build();
    }


    @Override
    public IncomeStatementResponseDto getIncomeStatement(String companyBusinessCode, LocalDate fromDate, LocalDate toDate) {
        // 1. جلب كافة حسابات الشركة
        List<Account> accounts = accountRepository.findByCompanyBusinessCode(companyBusinessCode);

        List<IncomeStatementItemDto> revenueItems = new ArrayList<>();
        List<IncomeStatementItemDto> expenseItems = new ArrayList<>();

        BigDecimal totalRevenues = BigDecimal.ZERO;
        BigDecimal totalExpenses = BigDecimal.ZERO;

        // 2. تصفية الحسابات التي تخص قائمة الدخل فقط (الإيرادات والمصروفات)
        for (Account account : accounts) {
            if (account.getAccountType() == AccountType.REVENUE || account.getAccountType() == AccountType.EXPENSE) {

                // تم تغيير var إلى اسم الكلاس الحقيقي للسطر (JournalEntryLine أو اسم الكلاس لديك)
                List<JournalEntryLine> lines = journalEntryLineRepository.findPostedLinesByAccountAndDateRange(
                        account.getId(), fromDate, toDate);

                // حساب رصيد الحساب للفترة
                BigDecimal accountPeriodBalance = lines.stream()
                        .map(line -> {
                            if (account.getAccountType() == AccountType.REVENUE) {
                                return line.getCredit().subtract(line.getDebit());
                            } else {
                                return line.getDebit().subtract(line.getCredit());
                            }
                        })
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                // تجاهل الحسابات التي رصيدها صفر خلال الفترة
                if (accountPeriodBalance.compareTo(BigDecimal.ZERO) != 0) {
                    IncomeStatementItemDto item = IncomeStatementItemDto.builder()
                            .accountId(account.getId())
                            .accountCode(account.getAccountCode())
                            .accountNameAr(account.getNameAr())
                            .accountType(account.getAccountType())
                            .amount(accountPeriodBalance)
                            .build();

                    if (account.getAccountType() == AccountType.REVENUE) {
                        revenueItems.add(item);
                        totalRevenues = totalRevenues.add(accountPeriodBalance);
                    } else {
                        expenseItems.add(item);
                        totalExpenses = totalExpenses.add(accountPeriodBalance);
                    }
                }
            }
        }

        // 3. حساب صافي الربح أو الخسارة
        BigDecimal netProfitOrLoss = totalRevenues.subtract(totalExpenses);
        boolean isProfitable = netProfitOrLoss.compareTo(BigDecimal.ZERO) >= 0;

        // 4. بناء الرد النهائي
        return IncomeStatementResponseDto.builder()
                .companyBusinessCode(companyBusinessCode)
                .fromDate(fromDate)
                .toDate(toDate)
                .revenues(revenueItems)
                .expenses(expenseItems)
                .totalRevenues(totalRevenues)
                .totalExpenses(totalExpenses)
                .netProfitOrLoss(netProfitOrLoss.abs())
                .isProfitable(isProfitable)
                .build();
    }
}
