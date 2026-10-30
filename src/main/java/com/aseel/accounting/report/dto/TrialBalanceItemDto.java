package com.aseel.accounting.report.dto;

import com.aseel.accounting.account.model.Enum.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrialBalanceItemDto {
    private Long accountId;
    private String accountCode;
    private String accountNameAr;
    private String accountNameEn;
    private AccountType accountType;
    private BigDecimal totalDebit;   // إجمالي المدين للحساب
    private BigDecimal totalCredit;  // إجمالي الدائن للحساب
    private BigDecimal netBalance;   // الرصيد الصافي (مدين أو دائن)
}
