package com.aseel.accounting.journal.controller;

import com.aseel.accounting.journal.dto.JournalEntryRequestDto;
import com.aseel.accounting.journal.dto.JournalEntryResponseDto;
import com.aseel.accounting.journal.service.JournalEntryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/journal-entries")
@RequiredArgsConstructor
public class JournalEntryController {

    private final JournalEntryService journalEntryService;

    // 1. إنشاء قيد يومية جديد (حالة DRAFT)
    @PostMapping
    public ResponseEntity<JournalEntryResponseDto> createJournalEntry(@Valid @RequestBody JournalEntryRequestDto requestDto) {
        JournalEntryResponseDto createdEntry = journalEntryService.createJournalEntry(requestDto);
        return new ResponseEntity<>(createdEntry, HttpStatus.CREATED);
    }

    // 2. جلب قيد محدد بواسطة المعرف ID
    @GetMapping("/{id}")
    public ResponseEntity<JournalEntryResponseDto> getJournalEntryById(@PathVariable Long id) {
        JournalEntryResponseDto journalEntry = journalEntryService.getJournalEntryById(id);
        return ResponseEntity.ok(journalEntry);
    }

    // 3. جلب جميع قيود شركة معينة بواسطة كود الشركة مع دعم التصفح (Pagination)
    @GetMapping("/company/{companyCode}")
    public ResponseEntity<Page<JournalEntryResponseDto>> getJournalEntriesByCompany(
            @PathVariable String companyCode,
            Pageable pageable) {
        Page<JournalEntryResponseDto> entries = journalEntryService.getJournalEntriesByCompany(companyCode, pageable);
        return ResponseEntity.ok(entries);
    }

    // 4. ترحيل القيد المحاسبي بواسطة رقم القيد (entryNumber)
    @PutMapping("/{entryNumber}/post")
    public ResponseEntity<JournalEntryResponseDto> postJournalEntry(@PathVariable String entryNumber) {
        JournalEntryResponseDto postedEntry = journalEntryService.postJournalEntry(entryNumber);
        return ResponseEntity.ok(postedEntry);
    }
}
