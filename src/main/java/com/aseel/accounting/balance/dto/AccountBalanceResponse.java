package com.aseel.accounting.balance.dto;

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
public class AccountBalanceResponse {

    private Long accountId;
    private String accountCode;
    private String accountName;
    private AccountType accountType;

    private BigDecimal totalDebit;   // إجمالي المدين المرحّل
    private BigDecimal totalCredit;  // إجمالي الدائن المرحّل
    private BigDecimal currentBalance; // الرصيد الصافي الحالي

    private String currency;         // العملة الأساسية للحساب (مثال: YER)
}
