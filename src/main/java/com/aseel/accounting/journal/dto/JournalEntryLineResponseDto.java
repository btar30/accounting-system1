package com.aseel.accounting.journal.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JournalEntryLineResponseDto {

//    private Long id;
    private Long accountId;
    private String accountCode;
    private String accountNameAr;
    private String accountNameEn;
    private BigDecimal debit;
    private BigDecimal credit;
    private String description;
}
