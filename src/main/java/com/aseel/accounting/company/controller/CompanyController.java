package com.aseel.accounting.company.controller;

import com.aseel.accounting.company.dto.request.CompanyRequestDTO;
import com.aseel.accounting.company.dto.response.CompanyResponseDTO;
import com.aseel.accounting.company.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/companies")
public class CompanyController {

    private final CompanyService companyService;

    // Constructor Injection (تم تعديله لاستخدام RequiredArgsConstructor)
    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    // =============================================
    // إنشاء شركة جديدة
    // =============================================
    @PostMapping
    public ResponseEntity<CompanyResponseDTO> createCompany(@Valid @RequestBody CompanyRequestDTO requestDTO) {
        log.info("📝 استلام طلب إنشاء شركة جديدة");
        CompanyResponseDTO createdCompany = companyService.createCompany(requestDTO);
        return new ResponseEntity<>(createdCompany, HttpStatus.CREATED);
    }

    // =============================================
    // البحث عن شركة بالكود
    // =============================================
    @GetMapping("/{businessCode}")
    public ResponseEntity<CompanyResponseDTO> getCompanyByBusinessCode(@PathVariable String businessCode) {
        log.info("🔍 استلام طلب البحث عن شركة بالكود: {}", businessCode);
        CompanyResponseDTO company = companyService.getCompanyByBusinessCode(businessCode);
        return ResponseEntity.ok(company);
    }

    // =============================================
    // جلب جميع الشركات
    // =============================================
    @GetMapping
    public ResponseEntity<List<CompanyResponseDTO>> getAllCompanies() {
        log.info("📋 استلام طلب جلب جميع الشركات");
        List<CompanyResponseDTO> companies = companyService.getAllCompanies();
        return ResponseEntity.ok(companies);
    }

    // =============================================
    // 🆕 جلب الشركات المعطلة
    // =============================================
    @GetMapping("/inactive")
    public ResponseEntity<List<CompanyResponseDTO>> getInactiveCompanies() {
        log.info("📋 استلام طلب جلب الشركات المعطلة");
        List<CompanyResponseDTO> companies = companyService.getInactiveCompanies();
        return ResponseEntity.ok(companies);
    }

    // =============================================
    // 🆕 جلب الشركات النشطة
    // =============================================
    @GetMapping("/active")
    public ResponseEntity<List<CompanyResponseDTO>> getActiveCompanies() {
        log.info("📋 استلام طلب جلب الشركات النشطة");
        List<CompanyResponseDTO> companies = companyService.getActiveCompanies();
        return ResponseEntity.ok(companies);
    }

    // =============================================
    // تعديل بيانات شركة
    // =============================================
    @PutMapping("/{businessCode}")
    public ResponseEntity<CompanyResponseDTO> updateCompany(
            @PathVariable String businessCode,
            @Valid @RequestBody CompanyRequestDTO requestDTO) {
        log.info("✏️ استلام طلب تعديل شركة بالكود: {}", businessCode);
        CompanyResponseDTO updatedCompany = companyService.updateCompany(businessCode, requestDTO);
        return ResponseEntity.ok(updatedCompany);
    }

    // =============================================
    // تعطيل شركة (موجود مسبقاً)
    // =============================================
    @DeleteMapping("/{businessCode}")
    public ResponseEntity<Void> disableCompany(@PathVariable String businessCode) {
        log.info("⛔ استلام طلب تعطيل شركة بالكود: {}", businessCode);
        companyService.disableCompany(businessCode);
        return ResponseEntity.noContent().build();
    }

    // =============================================
    // 🆕 إعادة تفعيل شركة
    // =============================================
    @PutMapping("/{businessCode}/reactivate")
    public ResponseEntity<CompanyResponseDTO> reactivateCompany(@PathVariable String businessCode) {
        log.info("🔄 استلام طلب إعادة تفعيل شركة بالكود: {}", businessCode);
        CompanyResponseDTO reactivatedCompany = companyService.reactivateCompany(businessCode);
        return ResponseEntity.ok(reactivatedCompany);
    }

    // =============================================
    // 🆕 حذف شركة نهائياً
    // =============================================
    @DeleteMapping("/{businessCode}/permanent")
    public ResponseEntity<Void> deleteCompany(@PathVariable String businessCode) {
        log.warn("🗑️ استلام طلب حذف شركة نهائياً بالكود: {}", businessCode);
        companyService.deleteCompany(businessCode);
        return ResponseEntity.noContent().build();
    }

    // =============================================
    // 🆕 حذف جميع الشركات نهائياً
    // =============================================
    @DeleteMapping("/all")
    public ResponseEntity<Void> deleteAllCompanies() {
        log.warn("🗑️🗑️🗑️ استلام طلب حذف جميع الشركات نهائياً!");
        companyService.deleteAllCompanies();
        return ResponseEntity.noContent().build();
    }

    // =============================================
    // 🆕 التحقق من وجود شركة
    // =============================================
    @GetMapping("/exists/{businessCode}")
    public ResponseEntity<Boolean> existsByBusinessCode(@PathVariable String businessCode) {
        log.debug("🔍 التحقق من وجود شركة بالكود: {}", businessCode);
        boolean exists = companyService.existsByBusinessCode(businessCode);
        return ResponseEntity.ok(exists);
    }
}