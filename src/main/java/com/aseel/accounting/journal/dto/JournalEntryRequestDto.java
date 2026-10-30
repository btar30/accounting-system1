package com.aseel.accounting.journal.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank; // تم التغيير لاستخدام NotBlank
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

        import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JournalEntryRequestDto {

    @NotNull(message = "تاريخ القيد مطلوب")
    private LocalDate entryDate;

    private String description;

    // --- التعديل هنا: Long -> String و NotBlank ---
    @NotBlank(message = "كود عمل الشركة مطلوب")
    private String companyBusinessCode;
    // ---------------------------------------------

    @NotEmpty(message = "يجب أن يحتوي القيد على سطرين على الأقل")
    @Valid
    private List<JournalEntryLineRequestDto> lines;
}
