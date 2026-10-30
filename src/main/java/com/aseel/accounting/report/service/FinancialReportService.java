package com.aseel.accounting.report.service;

import com.aseel.accounting.report.dto.IncomeStatementResponseDto;
import com.aseel.accounting.report.dto.TrialBalanceResponseDto;

import java.time.LocalDate;

public interface FinancialReportService {

    /**
     * توليد تقرير ميزان المراجعة لشركة معينة باستخدام كود الشركة التجاري
     */
    TrialBalanceResponseDto getTrialBalance(String companyBusinessCode);


    IncomeStatementResponseDto getIncomeStatement(String companyBusinessCode, LocalDate fromDate, LocalDate toDate);


}
