package com.aseel.accounting.account.service;

import com.aseel.accounting.account.dto.AccountRequestDto;
import com.aseel.accounting.account.dto.AccountResponseDto;
import com.aseel.accounting.account.dto.AccountTreeDto;

import java.util.List;

public interface AccountService {

    // 1. إنشاء حساب جديد
    AccountResponseDto createAccount(AccountRequestDto requestDto);

    // 2. جلب جميع حسابات شركة معينة (قائمة مسطحة)
    List<AccountResponseDto> getAccountsByCompany(String companyBusinessCode);

    // 3. جلب شجرة الحسابات الكاملة لشركة معينة (هيكل شجري هرمي)
    List<AccountTreeDto> getAccountTreeByCompany(String companyBusinessCode);

    // 4. جلب حساب محدد بواسطة الـ ID (يبقى كما هو لأنه ID الحساب نفسه)
    AccountResponseDto getAccountById(Long id);
}
