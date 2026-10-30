package com.aseel.accounting.company.model;

import com.aseel.accounting.common.base.BaseEntity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * كلاس (Entity) يمثل جدول الشركة في قاعدة البيانات.
 * يرث من BaseEntity للحصول على id, businessCode, وحقول Auditing تلقائياً.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder // يسهل إنشاء الكائنات بأسلوب نظيف
@Entity // يخبر JPA أن هذا الكلاس يمثل جدولاً
@Table(name = "companies") // تحديد اسم الجدول في قاعدة البيانات
public class Company extends BaseEntity {

    // 1. الاسم بالعربية والإنجليزية (Not Null)
    @Column(nullable = false, length = 150)
    private String nameAr;

    @Column(nullable = false, length = 150)
    private String nameEn;

    // 2. الرقم الضريبي (هام جداً للمحاسبة) - Unique
    @Column(unique = true, length = 50)
    private String taxNumber;

    // 3. بيانات الاتصال (اختيارية)
    private String address;

    @Column(length = 20)
    private String phone;

    @Column(length = 100)
    private String email;

    // 4. رابط الشعار
    private String logoUrl;

    // 5. حالة الشركة (نشطة/غير نشطة) - الافتراضي true
    @Column(nullable = false)
    private boolean isActive = true;
}
