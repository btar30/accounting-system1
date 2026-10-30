package com.aseel.accounting.journal.dto;

import com.aseel.accounting.journal.model.Enum.JournalEntryStatus;
import lombok.*;

        import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JournalEntryResponseDto {

    // private Long id; // مخفي بناءً على القرار
    private String entryNumber;
    private LocalDate entryDate;
    private String description;
    private JournalEntryStatus status;
    private BigDecimal totalDebit;
    private BigDecimal totalCredit;

    // --- التعديل هنا: Long companyId -> String companyBusinessCode ---
    private String companyBusinessCode;
    // -----------------------------------------------------------------

    private List<JournalEntryLineResponseDto> lines;
    private LocalDateTime createdAt;
}
