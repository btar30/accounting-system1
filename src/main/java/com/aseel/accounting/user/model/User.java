package com.aseel.accounting.user.model;

import com.aseel.accounting.common.base.BaseEntity;
import jakarta.persistence.*;
        import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String username; // اسم المستخدم أو البريد الإلكتروني لتسجيل الدخول

    @Column(nullable = false)
    private String password; // كلمة المرور المشفرة

    @Column(nullable = false, length = 150)
    private String fullName; // الاسم الكامل

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Role role;  // دور المستخدم في النظام

    // ===== حقل كود الشركه =====
    @Column(name = "business_code", nullable = false, unique = true, length = 50)
    private String businessCode;

    private String companyBusinessCode;// ربط المستخدم بشركة معينة (مهم)

    @Column(nullable = false)
    private boolean isActive = true; // حالة الحساب
}