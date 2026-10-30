//package com.aseel.accounting.account.dto;
//
//
//import com.aseel.accounting.account.model.Enum.AccountType;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.NotNull;
//import jakarta.validation.constraints.Size;
//import lombok.*;
//
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class AccountRequestDto {
//
//    @NotBlank(message = "اسم الحساب بالعربي مطلوب")
//    @Size(max = 150, message = "اسم الحساب يجب ألا يتجاوز 150 حرفاً")
//    private String nameAr;
//
//    @Size(max = 150, message = "اسم الحساب بالإنجليزية يجب ألا يتجاوز 150 حرفاً")
//    private String nameEn;
//
//    @NotNull(message = "نوع الحساب مطلوب")//هل اصول - حقوق - التزامات - حقوق- ايراقت - مصروفات
//    private AccountType accountType; // ASSET, LIABILITY, ...
//
//    @NotNull(message = "حدد ما إذا كان الحساب رئيسياً أم فرعياً") //أصل الحساب
//    private Boolean isParent; // true = رئيسي, false = فرعي
//
//    @NotNull(message = "معرف الشركة مطلوب")
//    private Long companyId;
//
//    private Long parentAccountId; // ID الحساب الأبُّ (يكون null لو كان حسابًا رئيسيًا من المستوى الأول)
//}
//
//
//
//

package com.aseel.accounting.account.dto;

import com.aseel.accounting.account.model.Enum.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountRequestDto {

    @NotBlank(message = "اسم الحساب بالعربي مطلوب")
    @Size(max = 150, message = "اسم الحساب يجب ألا يتجاوز 150 حرفاً")
    private String nameAr;

    @Size(max = 150, message = "اسم الحساب بالإنجليزية يجب ألا يتجاوز 150 حرفاً")
    private String nameEn;

    @NotNull(message = "نوع الحساب مطلوب") // هل اصول - حقوق - التزامات - ايرادات - مصروفات
    private AccountType accountType; // ASSET, LIABILITY, ...

    @NotNull(message = "حدد ما إذا كان الحساب رئيسياً أم فرعياً") // أصل الحساب
    private Boolean isParent; // true = رئيسي, false = فرعي

    // --- التعديل هنا ---
    @NotBlank(message = "كود عمل الشركة مطلوب") // نستخدم NotBlank للنصوص
    private String companyBusinessCode; // استقبال الـ Business Code بدلاً من ID
    // ------------------

    private Long parentAccountId; // ID الحساب الأبُّ (يكون null لو كان حسابًا رئيسيًا من المستوى الأول)
}

