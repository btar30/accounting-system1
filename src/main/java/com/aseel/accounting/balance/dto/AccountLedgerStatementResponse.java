package com.aseel.accounting.balance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountLedgerStatementResponse {

    private Long accountId;
    private String accountCode;
    private String accountName;

    private LocalDate fromDate;
    private LocalDate toDate;

    private BigDecimal openingBalance; // الرصيد الافتتاحي (قبل من تاريخ من المحدد)
    private BigDecimal closingBalance; // الرصيد النهائي حتى تاريخ إلى المحدد

    @Builder.Default
    private List<LedgerItemResponse> items = new ArrayList<>(); // قائمة الحركات التفصيلية
}
