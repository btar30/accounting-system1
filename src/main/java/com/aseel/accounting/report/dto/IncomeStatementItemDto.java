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
public class IncomeStatementItemDto {
    private Long accountId;
    private String accountCode;
    private String accountNameAr;
    private AccountType accountType; // REVENUE أو EXPENSE
    private BigDecimal amount;       // رصيد الحساب خلال الفترة المحددة
}
