package com.aseel.accounting.account.dto;

import com.aseel.accounting.account.model.Enum.AccountType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountResponseDto {

    private Long id;
    private String accountCode; // رمز الحساب الشجري مثل: 1101
    private String nameAr;// اسم الحساب بالعربي
    private String nameEn; // اسم الحساب باللغة الإنجليزية
    private AccountType accountType; //نوع الحساب
    private Boolean isParent; //أصل الحساب
    private Integer level;// مستوى الحساب في الشجرة (1, 2, 3...)
    private Boolean isActive; // حالة الحساب (نشيطٌ / موقف)
    private Long companyId;  // عِلاقة الحساب ب الشركة: كل حساب ينتمي لشركة واحدة
    private Long parentAccountId; // العِلاقة الذاتية ب الحساب الأبُّ (Parent)
    private String parentAccountNameAr; // العِلاقة الذاتية ب الحسابات الابن (Children)
}
