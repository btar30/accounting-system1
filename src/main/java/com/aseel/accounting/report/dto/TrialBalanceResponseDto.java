package com.aseel.accounting.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrialBalanceResponseDto {
    private String companyBusinessCode;
    private LocalDate reportDate;
    private List<TrialBalanceItemDto> items;

    private BigDecimal grandTotalDebit;  // إجمالي مجموع المدين لكل الحسابات
    private BigDecimal grandTotalCredit; // إجمالي مجموع الدائن لكل الحسابات
    private boolean isBalanced;          // هل الميزان متطابق (grandTotalDebit == grandTotalCredit)؟
}
