package com.aseel.accounting.user.dto;

import com.aseel.accounting.user.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class UserResponseDTO {
    private Long id;
    private String businessCode;
    private String username;
    private String fullName;
    private Role role;
    private String companyBusinessCode;
    private boolean isActive;
}
