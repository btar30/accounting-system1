package com.aseel.accounting.journal.service;

import com.aseel.accounting.journal.dto.JournalEntryRequestDto;
import com.aseel.accounting.journal.dto.JournalEntryResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JournalEntryService {

    // 1. إنشاء قيد يومية جديد (يبقى كما هو)
    JournalEntryResponseDto createJournalEntry(JournalEntryRequestDto requestDto);

    // 2. جلب قيد بواسطة المعرف (يبقى كما هو، معرف القيد نفسه)
    JournalEntryResponseDto getJournalEntryById(Long id);

    // 3. جلب قيود شركة معينة مع دعم التصفح
    // --- التعديل هنا: Long companyId -> String companyCode ---
    Page<JournalEntryResponseDto> getJournalEntriesByCompany(String companyCode, Pageable pageable);

//    // 4. ترحيل القيد (يبقى كما هو، معرف القيد نفسه)
//    JournalEntryResponseDto postJournalEntry(Long id);

    // تغيير المعامل من Long id إلى String entryNumber
    JournalEntryResponseDto postJournalEntry(String entryNumber);

}
