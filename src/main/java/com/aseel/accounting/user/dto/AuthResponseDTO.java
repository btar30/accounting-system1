package com.aseel.accounting.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDTO {
    private String token;
    private String username;
    private String fullName;
    private String role;
    private String companyBusinessCode; // معرف الشركة المرتبطة بالمستخدم (مهم جداً لمدير الشركة والمحاسب والكاشير)
}


