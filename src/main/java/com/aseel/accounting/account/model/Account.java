package com.aseel.accounting.account.model;

import com.aseel.accounting.account.model.Enum.AccountType;
import com.aseel.accounting.company.model.Company;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_code", nullable = false, length = 50)
    private String accountCode; // رمز الحساب الشجري مثل: 1101

    @Column(name = "name_ar", nullable = false, length = 150)
    private String nameAr; // اسم الحساب بالعربي

    @Column(name = "name_en", length = 150)
    private String nameEn; // اسم الحساب باللغة الإنجليزية

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false, length = 20)//نوع الحساب
    private AccountType accountType; // ASSET, LIABILITY, EQUITY, REVENUE, EXPENSE

    @Column(name = "is_parent", nullable = false) //أصل الحساب
    private Boolean isParent; // true = حساب رئيس (تجميعي)، false = حساب فرعي (تحليلي يتقبل قيود)

    @Column(name = "level", nullable = false)
    private Integer level; // مستوى الحساب في الشجرة (1, 2, 3...)

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true; // حالة الحساب (نشيطٌ / موقف)

    // العِلاقة ب الشركة: كل حساب ينتمي لشركة واحدة
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    // العِلاقة الذاتية ب الحساب الأبُّ (Parent)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Account parentAccount;

    // العِلاقة الذاتية ب الحسابات الابن (Children)
    @OneToMany(mappedBy = "parentAccount", cascade = CascadeType.ALL)
    private List<Account> subAccounts = new ArrayList<>();
}
