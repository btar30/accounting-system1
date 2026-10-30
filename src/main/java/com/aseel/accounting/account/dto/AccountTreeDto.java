package com.aseel.accounting.account.dto;

import com.aseel.accounting.account.model.Enum.AccountType;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountTreeDto { // ديتا ء الخاص بانشاء الابناء التابعين لحساب ألأب

    private Long id;
    private String accountCode;
    private String nameAr;
    private String nameEn;
    private AccountType accountType;
    private Boolean isParent;
    private Integer level;
    private Boolean isActive;

    @Builder.Default
    private List<AccountTreeDto> children = new ArrayList<>(); // الأبناء التابعين لهذا الحساب
}
