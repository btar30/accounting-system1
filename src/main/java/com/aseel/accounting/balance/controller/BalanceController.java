package com.aseel.accounting.balance.controller;

import com.aseel.accounting.balance.dto.AccountBalanceResponse;
import com.aseel.accounting.balance.dto.AccountLedgerStatementResponse;
import com.aseel.accounting.balance.service.BalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/balances")
@RequiredArgsConstructor
public class BalanceController {

    private final BalanceService balanceService;

    /**
     * 1. استعلام الرصيد اللحظي الصافي لحساب معين
     * Example: GET /api/v1/balances/account/5?companyId=1
     */
    @GetMapping("/account/{accountId}")
    public ResponseEntity<AccountBalanceResponse> getAccountBalance(
            @RequestParam Long companyId,
            @PathVariable Long accountId) {

        AccountBalanceResponse response = balanceService.getAccountBalance(companyId, accountId);
        return ResponseEntity.ok(response);
    }

    /**
     * 2. استعلام كشف حساب تفصيلي خلال فترة زمنية
     * Example: GET /api/v1/balances/account/5/statement?companyId=1&fromDate=2026-01-01&toDate=2026-07-30
     */
    @GetMapping("/account/{accountId}/statement")
    public ResponseEntity<AccountLedgerStatementResponse> getAccountLedgerStatement(
            @RequestParam Long companyId,
            @PathVariable Long accountId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        AccountLedgerStatementResponse response = balanceService.getAccountLedgerStatement(
                companyId, accountId, fromDate, toDate);

        return ResponseEntity.ok(response);
    }

    /**
     * 3. استعلام أرصدة كافة حسابات شركة معينة
     * Example: GET /api/v1/balances/company/1
     */
    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<AccountBalanceResponse>> getAllCompanyBalances(
            @PathVariable Long companyId) {

        List<AccountBalanceResponse> responses = balanceService.getAllAccountBalancesByCompany(companyId);
        return ResponseEntity.ok(responses);
    }

}
