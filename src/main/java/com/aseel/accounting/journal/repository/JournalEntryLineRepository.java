package com.aseel.accounting.journal.repository;

import com.aseel.accounting.journal.model.Enum.JournalEntryStatus;
import com.aseel.accounting.journal.model.JournalEntryLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface JournalEntryLineRepository extends JpaRepository<JournalEntryLine, Long> {

    List<JournalEntryLine> findByAccountId(Long accountId);

    @Query("SELECT line FROM JournalEntryLine line " +
            "WHERE line.account.id = :accountId " +
            "AND line.journalEntry.status = com.aseel.accounting.journal.model.Enum.JournalEntryStatus.POSTED " +
            "AND line.journalEntry.entryDate BETWEEN :startDate AND :endDate")
    List<JournalEntryLine> findPostedLinesByAccountAndDateRange(
            @Param("accountId") Long accountId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT line FROM JournalEntryLine line WHERE line.journalEntry.company.businessCode = :companyBusinessCode")
    List<JournalEntryLine> findByCompanyBusinessCode(@Param("companyBusinessCode") String companyBusinessCode);


    // === الدواّل الحسابية بعد التعديل (إلغاء شرط company.id المعقد أو ربطه بالـ account مباشرة) ===

    // 1. إجمالي المدين لحساب معين للقيود المرحّلة
    @Query("SELECT COALESCE(SUM(l.debit), 0) FROM JournalEntryLine l " +
            "WHERE l.account.id = :accountId " +
            "AND l.journalEntry.status = :status")
    BigDecimal sumDebitByAccountIdAndStatus(@Param("accountId") Long accountId,
                                            @Param("status") JournalEntryStatus status);

    // 2. إجمالي الدائن لحساب معين للقيود المرحّلة
    @Query("SELECT COALESCE(SUM(l.credit), 0) FROM JournalEntryLine l " +
            "WHERE l.account.id = :accountId " +
            "AND l.journalEntry.status = :status")
    BigDecimal sumCreditByAccountIdAndStatus(@Param("accountId") Long accountId,
                                             @Param("status") JournalEntryStatus status);

    // 3. الرصيد الافتتاحي (صافي الحركة السابقة قبل تاريخ معين)
    @Query("SELECT COALESCE(SUM(l.debit - l.credit), 0) FROM JournalEntryLine l " +
            "WHERE l.account.id = :accountId " +
            "AND l.journalEntry.status = :status " +
            "AND l.journalEntry.entryDate < :fromDate")
    BigDecimal calculateOpeningBalance(@Param("accountId") Long accountId,
                                       @Param("fromDate") LocalDate fromDate,
                                       @Param("status") JournalEntryStatus status);

    // 4. جلب حركات الحساب خلال فترة مع الترتيب الزمني الدقيق (لبناء الرصيد التراكمي)
    @Query("SELECT l FROM JournalEntryLine l " +
            "WHERE l.account.id = :accountId " +
            "AND l.journalEntry.status = :status " +
            "AND l.journalEntry.entryDate BETWEEN :fromDate AND :toDate " +
            "ORDER BY l.journalEntry.entryDate ASC, l.journalEntry.id ASC")
    List<JournalEntryLine> findLedgerLinesOrdered(@Param("accountId") Long accountId,
                                                  @Param("fromDate") LocalDate fromDate,
                                                  @Param("toDate") LocalDate toDate,
                                                  @Param("status") JournalEntryStatus status);
}
