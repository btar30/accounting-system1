package com.aseel.accounting.balance.service.Impl;

import com.aseel.accounting.account.model.Account;
import com.aseel.accounting.account.model.Enum.AccountType;
import com.aseel.accounting.account.repository.AccountRepository;
import com.aseel.accounting.balance.dto.AccountBalanceResponse;
import com.aseel.accounting.balance.dto.AccountLedgerStatementResponse;
import com.aseel.accounting.balance.dto.LedgerItemResponse;
import com.aseel.accounting.balance.service.BalanceService;
import com.aseel.accounting.journal.model.Enum.JournalEntryStatus;
import com.aseel.accounting.journal.model.JournalEntryLine;
import com.aseel.accounting.journal.repository.JournalEntryLineRepository;
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
public class BalanceServiceImpl implements BalanceService {

    private final JournalEntryLineRepository journalEntryLineRepository;
    private final AccountRepository accountRepository;

    @Override
    public AccountBalanceResponse getAccountBalance(Long companyId, Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("الحساب غير موجود: " + accountId));

        // تعديل: حذف companyId من الاستدعاء
        BigDecimal totalDebit = journalEntryLineRepository.sumDebitByAccountIdAndStatus(
                accountId, JournalEntryStatus.POSTED);

        // تعديل: حذف companyId من الاستدعاء
        BigDecimal totalCredit = journalEntryLineRepository.sumCreditByAccountIdAndStatus(
                accountId, JournalEntryStatus.POSTED);

        BigDecimal currentBalance = calculateBalanceByNature(account.getAccountType(), totalDebit, totalCredit);

        return AccountBalanceResponse.builder()
                .accountId(account.getId())
                .accountCode(account.getAccountCode())
                .accountName(account.getNameAr())
                .accountType(account.getAccountType())
                .totalDebit(totalDebit)
                .totalCredit(totalCredit)
                .currentBalance(currentBalance)
                .currency("YER")
                .build();
    }

    @Override
    public AccountLedgerStatementResponse getAccountLedgerStatement(
            Long companyId, Long accountId, LocalDate fromDate, LocalDate toDate) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("الحساب غير موجود: " + accountId));

        // تعديل: حذف companyId من الاستدعاء
        BigDecimal rawOpening = journalEntryLineRepository.calculateOpeningBalance(
                accountId, fromDate, JournalEntryStatus.POSTED);

        BigDecimal openingBalance = isDebitNature(account.getAccountType()) ? rawOpening : rawOpening.negate();

        // تعديل: حذف companyId من الاستدعاء
        List<JournalEntryLine> lines = journalEntryLineRepository.findLedgerLinesOrdered(
                accountId, fromDate, toDate, JournalEntryStatus.POSTED);

        List<LedgerItemResponse> items = new ArrayList<>();
        BigDecimal runningBalance = openingBalance;

        for (JournalEntryLine line : lines) {
            BigDecimal debit = line.getDebit();
            BigDecimal credit = line.getCredit();

            if (isDebitNature(account.getAccountType())) {
                runningBalance = runningBalance.add(debit).subtract(credit);
            } else {
                runningBalance = runningBalance.add(credit).subtract(debit);
            }

            items.add(LedgerItemResponse.builder()
                    .entryDate(line.getJournalEntry().getEntryDate())
                    .entryNumber(line.getJournalEntry().getEntryNumber())
                    .description(line.getDescription() != null ? line.getDescription() : line.getJournalEntry().getDescription())
                    .debit(debit)
                    .credit(credit)
                    .runningBalance(runningBalance)
                    .build());
        }

        return AccountLedgerStatementResponse.builder()
                .accountId(account.getId())
                .accountCode(account.getAccountCode())
                .accountName(account.getNameAr())
                .fromDate(fromDate)
                .toDate(toDate)
                .openingBalance(openingBalance)
                .closingBalance(runningBalance)
                .items(items)
                .build();
    }

    @Override
    public List<AccountBalanceResponse> getAllAccountBalancesByCompany(Long companyId) {
        List<Account> accounts = accountRepository.findByCompanyId(companyId);

        List<AccountBalanceResponse> responses = new ArrayList<>();

        for (Account account : accounts) {
            // تعديل: حذف companyId من الاستدعاء
            BigDecimal totalDebit = journalEntryLineRepository.sumDebitByAccountIdAndStatus(
                    account.getId(), JournalEntryStatus.POSTED);

            // تعديل: حذف companyId من الاستدعاء
            BigDecimal totalCredit = journalEntryLineRepository.sumCreditByAccountIdAndStatus(
                    account.getId(), JournalEntryStatus.POSTED);

            BigDecimal currentBalance = calculateBalanceByNature(account.getAccountType(), totalDebit, totalCredit);

            responses.add(AccountBalanceResponse.builder()
                    .accountId(account.getId())
                    .accountCode(account.getAccountCode())
                    .accountName(account.getNameAr())
                    .accountType(account.getAccountType())
                    .totalDebit(totalDebit)
                    .totalCredit(totalCredit)
                    .currentBalance(currentBalance)
                    .currency("YER")
                    .build());
        }

        return responses;
    }

    private BigDecimal calculateBalanceByNature(AccountType type, BigDecimal debit, BigDecimal credit) {
        return isDebitNature(type) ? debit.subtract(credit) : credit.subtract(debit);
    }

    private boolean isDebitNature(AccountType type) {
        return type == AccountType.ASSET || type == AccountType.EXPENSE;
    }
}
