package com.aseel.accounting.company.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * كائن نقل البيانات لعرض معلومات الشركة.
 */
@Data
public class CompanyResponseDTO {

    // قرارنا المعماري: نستخدم الـ businessCode كنظام تعريف وليس الـ ID

    private Long id;

    private String businessCode;

    private String nameAr;
    private String nameEn;
    private String taxNumber;
    private String address;
    private String phone;
    private String email;
    private String logoUrl;
    private boolean isActive;

    // نعرض تاريخ الإنشاء أيضاً
    private LocalDateTime createdAt;
}
