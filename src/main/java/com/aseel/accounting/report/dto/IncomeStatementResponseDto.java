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
public class IncomeStatementResponseDto {
    private String companyBusinessCode;
    private LocalDate fromDate;
    private LocalDate toDate;

    private List<IncomeStatementItemDto> revenues; // قائمة الإيرادات
    private List<IncomeStatementItemDto> expenses; // قائمة المصروفات

    private BigDecimal totalRevenues; // إجمالي الإيرادات
    private BigDecimal totalExpenses; // إجمالي المصروفات
    private BigDecimal netProfitOrLoss; // صافي الربح (الإيرادات - المصروفات)
    private boolean isProfitable;     // هل هو ربح أم خسارة؟
}
