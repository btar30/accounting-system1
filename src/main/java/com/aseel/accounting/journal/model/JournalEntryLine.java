package com.aseel.accounting.journal.model;

import com.aseel.accounting.account.model.Account;
import com.aseel.accounting.common.base.BaseEntity; // مسار الباكيج الصحيح للـ BaseEntity
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "journal_entry_lines", indexes = {
        @Index(name = "idx_jel_entry_id", columnList = "journal_entry_id"),
        @Index(name = "idx_jel_account_id", columnList = "account_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class JournalEntryLine extends BaseEntity {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;

    //العلاقة مع القيد الرئيسي (journalEntry):
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "journal_entry_id", nullable = false)
    private JournalEntry journalEntry;

    //العِلاقة مع الحساب المحاسبي (account):
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    //المبالغ المالية (المدين):
    @Column(name = "debit", nullable = false, precision = 18, scale = 4)
    @Builder.Default
    private BigDecimal debit = BigDecimal.ZERO;

    //المبالغ المالية (الدائن):
    @Column(name = "credit", nullable = false, precision = 18, scale = 4)
    @Builder.Default
    private BigDecimal credit = BigDecimal.ZERO;

    // البيان الخاص بالسطر (description):
    @Column(name = "description", length = 500)
    private String description;
}
