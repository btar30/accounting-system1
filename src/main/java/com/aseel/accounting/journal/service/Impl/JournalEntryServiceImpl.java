package com.aseel.accounting.journal.service.Impl;

import com.aseel.accounting.account.model.Account;
import com.aseel.accounting.account.repository.AccountRepository;
import com.aseel.accounting.company.model.Company;
import com.aseel.accounting.company.repository.CompanyRepository;
import com.aseel.accounting.exception.BusinessRuleException;
import com.aseel.accounting.exception.ResourceNotFoundException;
import com.aseel.accounting.journal.dto.JournalEntryLineRequestDto;
import com.aseel.accounting.journal.dto.JournalEntryLineResponseDto;
import com.aseel.accounting.journal.dto.JournalEntryRequestDto;
import com.aseel.accounting.journal.dto.JournalEntryResponseDto;
import com.aseel.accounting.journal.model.JournalEntry;
import com.aseel.accounting.journal.model.JournalEntryLine;
import com.aseel.accounting.journal.model.Enum.JournalEntryStatus;
import com.aseel.accounting.journal.repository.JournalEntryRepository;
import com.aseel.accounting.journal.service.JournalEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class JournalEntryServiceImpl implements JournalEntryService {

    private final JournalEntryRepository journalEntryRepository;
    private final AccountRepository accountRepository;
    private final CompanyRepository companyRepository;

    //===================================================
    // 1. إنشاء قيد يومية جديد (حالة DRAFT)
    //===================================================
    @Override
    public JournalEntryResponseDto createJournalEntry(JournalEntryRequestDto requestDto) {
        Company company = companyRepository.findByBusinessCode(requestDto.getCompanyBusinessCode())
                .orElseThrow(() -> new ResourceNotFoundException("الشركة غير موجودة بالكود: " + requestDto.getCompanyBusinessCode()));

        BigDecimal totalDebit = BigDecimal.ZERO;
        BigDecimal totalCredit = BigDecimal.ZERO;

        List<JournalEntryLine> lines = new ArrayList<>();

        for (JournalEntryLineRequestDto lineDto : requestDto.getLines()) {
            Account account = accountRepository.findById(lineDto.getAccountId())
                    .orElseThrow(() -> new ResourceNotFoundException("الحساب غير موجود بالمعرف: " + lineDto.getAccountId()));

            if (Boolean.TRUE.equals(account.getIsParent())) {
                throw new BusinessRuleException("لا يمكن إضافة قيد على حساب رئيسي: " + account.getNameAr() + " (" + account.getAccountCode() + ")");
            }

            if ((lineDto.getDebit().compareTo(BigDecimal.ZERO) > 0 && lineDto.getCredit().compareTo(BigDecimal.ZERO) > 0) ||
                    (lineDto.getDebit().compareTo(BigDecimal.ZERO) == 0 && lineDto.getCredit().compareTo(BigDecimal.ZERO) == 0)) {
                throw new BusinessRuleException("سطر القيد يجب أن يحتوي إما على مبلغ مدين أو دائن فقط");
            }

            totalDebit = totalDebit.add(lineDto.getDebit());
            totalCredit = totalCredit.add(lineDto.getCredit());

            JournalEntryLine line = JournalEntryLine.builder()
                    .businessCode("JEL-" + UUID.randomUUID().toString().substring(0, 8))
                    .account(account)
                    .debit(lineDto.getDebit())
                    .credit(lineDto.getCredit())
                    .description(lineDto.getDescription())
                    .build();

            lines.add(line);
        }

        if (totalDebit.compareTo(totalCredit) != 0) {
            throw new BusinessRuleException("القيد غير متوازن! إجمالي المدين: " + totalDebit + " لا يساوي إجمالي الدائن: " + totalCredit);
        }

        String entryNumber = generateEntryNumber(company, requestDto.getEntryDate().getYear());

        JournalEntry journalEntry = JournalEntry.builder()
                .businessCode(entryNumber)
                .entryNumber(entryNumber)
                .entryDate(requestDto.getEntryDate())
                .description(requestDto.getDescription())
                .status(JournalEntryStatus.DRAFT)
                .totalDebit(totalDebit)
                .totalCredit(totalCredit)
                .company(company)
                .build();

        lines.forEach(journalEntry::addLine);

        JournalEntry savedEntry = journalEntryRepository.save(journalEntry);

        return mapToResponseDto(savedEntry);
    }

    //===================================================
    // 2. جلب قيد محدد بواسطة المعرف ID
    //===================================================
    @Override
    @Transactional(readOnly = true)
    public JournalEntryResponseDto getJournalEntryById(Long id) {
        JournalEntry journalEntry = journalEntryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("القيد غير موجود بالمعرف: " + id));
        return mapToResponseDto(journalEntry);
    }

    //===================================================
    // 3. جلب جميع قيود شركة معينة بواسطة كود الشركة مع دعم التصفح (Pagination)
    //===================================================
    @Override
    @Transactional(readOnly = true)
    public Page<JournalEntryResponseDto> getJournalEntriesByCompany(String companyCode, Pageable pageable) {
        return journalEntryRepository.findByCompanyBusinessCode(companyCode, pageable)
                .map(this::mapToResponseDto);
    }

    //===================================================
    // 4. ترحيل القيد المحاسبي بواسطة رقم القيد (entryNumber)
    //===================================================
    // --- التعديل هنا: الترحيل باستخدام entryNumber بدلاً من ID ---
    @Override
    public JournalEntryResponseDto postJournalEntry(String entryNumber) {
        JournalEntry journalEntry = journalEntryRepository.findByEntryNumber(entryNumber)
                .orElseThrow(() -> new ResourceNotFoundException("القيد غير موجود برقم: " + entryNumber));

        if (journalEntry.getStatus() == JournalEntryStatus.POSTED) {
            throw new BusinessRuleException("القيد مرحّل بالفعل مسبقاً ولا يمكن إعادة ترحيله");
        }

        journalEntry.setStatus(JournalEntryStatus.POSTED);
        JournalEntry updatedEntry = journalEntryRepository.save(journalEntry);

        return mapToResponseDto(updatedEntry);
    }

    private String generateEntryNumber(Company company, int year) {
        String prefix = company.getBusinessCode() + "-JV-" + year + "-";
        String maxNumber = journalEntryRepository.findMaxEntryNumberByCompanyIdAndPrefix(company.getId(), prefix);

        if (maxNumber == null) {
            return prefix + "0001";
        }

        String sequenceStr = maxNumber.substring(prefix.length());
        int nextSequence = Integer.parseInt(sequenceStr) + 1;
        return prefix + String.format("%04d", nextSequence);
    }

    private JournalEntryResponseDto mapToResponseDto(JournalEntry entry) {
        List<JournalEntryLineResponseDto> lineDtos = entry.getLines().stream()
                .map(line -> JournalEntryLineResponseDto.builder()
                        .accountId(line.getAccount().getId())
                        .accountCode(line.getAccount().getAccountCode())
                        .accountNameAr(line.getAccount().getNameAr())
                        .accountNameEn(line.getAccount().getNameEn())
                        .debit(line.getDebit())
                        .credit(line.getCredit())
                        .description(line.getDescription())
                        .build())
                .toList();

        return JournalEntryResponseDto.builder()
                .entryNumber(entry.getEntryNumber())
                .entryDate(entry.getEntryDate())
                .description(entry.getDescription())
                .status(entry.getStatus())
                .totalDebit(entry.getTotalDebit())
                .totalCredit(entry.getTotalCredit())
                .companyBusinessCode(entry.getCompany().getBusinessCode())
                .lines(lineDtos)
                .createdAt(entry.getCreatedAt())
                .build();
    }
}
