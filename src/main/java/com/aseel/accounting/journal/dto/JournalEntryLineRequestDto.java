package com.aseel.accounting.journal.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JournalEntryLineRequestDto {

    @NotNull(message = "معرف الحساب مطلوب")
    private Long accountId; // <-- تأكد من مطابقة الاسم والتسمية بالضبط

    @NotNull(message = "مبلغ المدين مطلوب")
    @PositiveOrZero(message = "يجب أن يكون مبلغ المدين صفراً أو أكبر")
    private BigDecimal debit;

    @NotNull(message = "مبلغ الدائن مطلوب")
    @PositiveOrZero(message = "يجب أن يكون مبلغ الدائن صفراً أو أكبر")
    private BigDecimal credit;

    private String description;
}




