package com.aseel.accounting.journal.model;

import com.aseel.accounting.common.base.BaseEntity;
import com.aseel.accounting.company.model.Company;
import com.aseel.accounting.journal.model.Enum.JournalEntryStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "journal_entries", indexes = {
        @Index(name = "idx_je_company_date", columnList = "company_id, entry_date"),
        @Index(name = "idx_je_number", columnList = "entry_number")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class JournalEntry extends BaseEntity { //رأس القيد / القيد اليومي

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;

    @Column(name = "entry_number", nullable = false, length = 50)
    private String entryNumber; // رقم القيد المولد تلقائياً (مثال: JV-2026-0001)

    @Column(name = "entry_date", nullable = false)
    private LocalDate entryDate; // تاريخ القيد

    @Column(name = "description", length = 500)
    private String description; // سبب ألعمليه البيان العام للقيد

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private JournalEntryStatus status = JournalEntryStatus.DRAFT; //حاله القيد (مسوده - مرحله - ملغيه)(DRAFT- POSTED - CANCELLED )

    @Column(name = "total_debit", nullable = false, precision = 18, scale = 4)
    @Builder.Default
    private BigDecimal totalDebit = BigDecimal.ZERO; // إجمالي المدين

    @Column(name = "total_credit", nullable = false, precision = 18, scale = 4)
    @Builder.Default
    private BigDecimal totalCredit = BigDecimal.ZERO; // إجمالي الدائن

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company; // الشركة التابع لها القيد

    @OneToMany(mappedBy = "journalEntry", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<JournalEntryLine> lines = new ArrayList<>(); // أسطر/تفاصيل القيد

    // دالة مساعدة لإضافة سطر للقيد
    public void addLine(JournalEntryLine line) {
        lines.add(line);
        line.setJournalEntry(this);
    }

    // دالة مساعدة لإزالة سطر من القيد
    public void removeLine(JournalEntryLine line) {
        lines.remove(line);
        line.setJournalEntry(null);
    }
}
