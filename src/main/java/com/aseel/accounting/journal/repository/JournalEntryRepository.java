package com.aseel.accounting.journal.repository;

import com.aseel.accounting.journal.model.JournalEntry;
import com.aseel.accounting.journal.model.Enum.JournalEntryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JournalEntryRepository extends JpaRepository<JournalEntry, Long> {

    // === الدواّل الجديدة (باستخدام businessCode) ===

    // 1. جلب قيود شركة معينة بواسطة الكود مع دعم التصفح
    // Spring Data JPA سيفهم تلقائيًا أنه يجب البحث في حقل businessCode داخل كائن company المرتبط
    Page<JournalEntry> findByCompanyBusinessCode(String businessCode, Pageable pageable);

    // 2. جلب قيود شركة معينة بحالة محددة بواسطة الكود
    Page<JournalEntry> findByCompanyBusinessCodeAndStatus(String businessCode, JournalEntryStatus status, Pageable pageable);

    // 3. البحث عن قيد برقم القيد وكود الشركة
    Optional<JournalEntry> findByEntryNumberAndCompanyBusinessCode(String entryNumber, String businessCode);


    // === الدواّل القديمة (باستخدام ID) ===
    // نحتفظ بها للاستخدام الداخلي داخل طبقة الـ Service إذا لزم الأمر

    Page<JournalEntry> findByCompanyId(Long companyId, Pageable pageable);

    Page<JournalEntry> findByCompanyIdAndStatus(Long companyId, JournalEntryStatus status, Pageable pageable);

    Optional<JournalEntry> findByEntryNumberAndCompanyId(String entryNumber, Long companyId);

    // تم تعديل اسم الدالة قليلاً ليكون أكثر وضوحاً (إضافة Id)
    @Query("SELECT MAX(j.entryNumber) FROM JournalEntry j WHERE j.company.id = :companyId AND j.entryNumber LIKE :prefix%")
    String findMaxEntryNumberByCompanyIdAndPrefix(@Param("companyId") Long companyId, @Param("prefix") String prefix);


    // أضف هذه الدالة إن لم تكن موجودة مسبقاً
    Optional<JournalEntry> findByEntryNumber(String entryNumber);

}
