package com.aseel.accounting.company.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * كائن نقل البيانات لإنشاء أو تعديل شركة.
 */
@Data
public class CompanyRequestDTO {

    private Long id;

    @NotBlank(message = "الاسم بالعربية مطلوب")
    @Size(max = 150, message = "الاسم بالعربية لا يجب أن يتجاوز 150 حرف")
    private String nameAr;

    @NotBlank(message = "الاسم بالإنجليزية مطلوب")
    @Size(max = 150, message = "الاسم بالإنجليزية لا يجب أن يتجاوز 150 حرف")
    private String nameEn;

    @NotBlank(message = "الرقم الضريبي مطلوب")
    @Size(max = 50, message = "الرقم الضريبي لا يجب أن يتجاوز 50 حرف")
    private String taxNumber;

    private String address;

    @Size(max = 20, message = "رقم الهاتف غير صحيح")
    private String phone;

    @Email(message = "البريد الإلكتروني غير صحيح")
    @Size(max = 100, message = "البريد الإلكتروني لا يجب أن يتجاوز 100 حرف")
    private String email;

    private String logoUrl;
}
