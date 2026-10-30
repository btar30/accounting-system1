package com.aseel.accounting.balance.service;

import com.aseel.accounting.balance.dto.AccountBalanceResponse;
import com.aseel.accounting.balance.dto.AccountLedgerStatementResponse;
import java.util.List;
import java.time.LocalDate;

public interface BalanceService {

    //===================================================
    // حساب الرصيد اللحظي الحالي لحساب معين
    //===================================================
    AccountBalanceResponse getAccountBalance(Long companyId, Long accountId);

    //===================================================
    // إنشاء كشف حساب تفصيلي خلال فترة زمنية مع الرصيد الجاري المتراكم
    //===================================================
    AccountLedgerStatementResponse getAccountLedgerStatement(
            Long companyId, Long accountId, LocalDate fromDate, LocalDate toDate);

    //===================================================
    // جلب أرصدة كل حسابات الشركة
    //===================================================
    List<AccountBalanceResponse> getAllAccountBalancesByCompany(Long companyId);

}
