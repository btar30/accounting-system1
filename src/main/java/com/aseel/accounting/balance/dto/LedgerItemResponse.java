package com.aseel.accounting.balance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LedgerItemResponse {

    private LocalDate entryDate;
    private String entryNumber;      // رقم القيد (مثل JV-2026-0001)
    private String description;      // بيان الحركة التفصيلي
    private BigDecimal debit;        // مبلغ مدين الحركة
    private BigDecimal credit;       // مبلغ دائن الحركة
    private BigDecimal runningBalance; // الرصيد المتراكم بعد الحركة مباشرة
}
