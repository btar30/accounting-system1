package com.aseel.accounting.report.controller;

import com.aseel.accounting.report.dto.IncomeStatementResponseDto;
import com.aseel.accounting.report.dto.TrialBalanceResponseDto;
import com.aseel.accounting.report.service.FinancialReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class FinancialReportController {

    private final FinancialReportService financialReportService;

    /**
     * استعلام ميزان المراجعة لشركة معينة بناءً على كود الشركة التجاري
     * Example: GET /api/v1/reports/trial-balance?companyBusinessCode=COMP-123
     */
    @GetMapping("/trial-balance")
    public ResponseEntity<TrialBalanceResponseDto> getTrialBalance(
            @RequestParam String companyBusinessCode) {

        TrialBalanceResponseDto response = financialReportService.getTrialBalance(companyBusinessCode);
        return ResponseEntity.ok(response);
    }


    /**
     * استعلام قائمة الدخل (الأرباح والخسائر) خلال فترة زمنية محددة
     * Example: GET /api/v1/reports/income-statement?companyBusinessCode=COMP-123&fromDate=2026-01-01&toDate=2026-08-05
     */
    @GetMapping("/income-statement")
    public ResponseEntity<IncomeStatementResponseDto> getIncomeStatement(
            @RequestParam String companyBusinessCode,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        IncomeStatementResponseDto response = financialReportService.getIncomeStatement(companyBusinessCode, fromDate, toDate);
        return ResponseEntity.ok(response);
    }

}
