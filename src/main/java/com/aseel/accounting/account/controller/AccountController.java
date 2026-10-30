package com.aseel.accounting.account.controller;

import com.aseel.accounting.account.dto.AccountRequestDto;
import com.aseel.accounting.account.dto.AccountResponseDto;
import com.aseel.accounting.account.dto.AccountTreeDto;
import com.aseel.accounting.account.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    //===============================================
    // 1. إنشاء حساب جديد (رئيسي أو فرعي)
    //===============================================
    @PostMapping
    public ResponseEntity<AccountResponseDto> createAccount(@Valid @RequestBody AccountRequestDto requestDto) {
        // سيعمل تلقائياً بعد تعديل الـ DTO و الـ Service
        AccountResponseDto createdAccount = accountService.createAccount(requestDto);
        return new ResponseEntity<>(createdAccount, HttpStatus.CREATED);
    }

    //===============================================
    // 2. جلب شجرة الحسابات الكاملة لشركة معينة (هيكل هرمي)
    //===============================================
    @GetMapping("/company/{companyCode}/tree")
    public ResponseEntity<List<AccountTreeDto>> getAccountTreeByCompany(@PathVariable String companyCode) {
        List<AccountTreeDto> tree = accountService.getAccountTreeByCompany(companyCode);
        return ResponseEntity.ok(tree);
    }

    //===============================================
    // 3. جلب جميع حسابات شركة معينة (قائمة مسطحة)
    //===============================================
    @GetMapping("/company/{companyCode}")
    public ResponseEntity<List<AccountResponseDto>> getAccountsByCompany(@PathVariable String companyCode) {
        List<AccountResponseDto> accounts = accountService.getAccountsByCompany(companyCode);
        return ResponseEntity.ok(accounts);
    }

    //===============================================
    // 4. جلب تفاصيل حساب واحد بواسطة المعرف
    //===============================================
    @GetMapping("/{id}")
    public ResponseEntity<AccountResponseDto> getAccountById(@PathVariable Long id) {
        // يبقى كما هو لأنه ID الحساب نفسه
        AccountResponseDto account = accountService.getAccountById(id);
        return ResponseEntity.ok(account);
    }
}
