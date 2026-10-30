package com.aseel.accounting.user.dto;

import com.aseel.accounting.user.model.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequestDTO {
    private String username;
    private String password;
    private String fullName;
    private Role role;
    private String businessCode;
    private String companyBusinessCode;
}
