package com.aseel.accounting.company.service.impl;

import com.aseel.accounting.company.dto.request.CompanyRequestDTO;
import com.aseel.accounting.company.dto.response.CompanyResponseDTO;
import com.aseel.accounting.company.model.Company;
import com.aseel.accounting.company.repository.CompanyRepository;
import com.aseel.accounting.company.service.CompanyService;
import com.aseel.accounting.exception.DuplicateResourceException;
import com.aseel.accounting.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;

    // ===========================================
    // إضافة شركه
    // ===========================================
    @Override
    public CompanyResponseDTO createCompany(CompanyRequestDTO requestDTO) {
        log.info("محاولة إنشاء شركة جديدة: {}", requestDTO.getNameAr());

        // 1. التحقق من الرقم الضريبي (منع التكرار)
        if (companyRepository.existsByTaxNumber(requestDTO.getTaxNumber())) {
            log.warn("الرقم الضريبي موجود مسبقاً: {}", requestDTO.getTaxNumber());
            throw new DuplicateResourceException("الرقم الضريبي (" + requestDTO.getTaxNumber() + ") مُسجّل مسبقاً في النظام");
        }

        // 2. التحويل اليدوي من DTO إلى Entity
        Company company = Company.builder()
                .nameAr(requestDTO.getNameAr())
                .nameEn(requestDTO.getNameEn())
                .taxNumber(requestDTO.getTaxNumber())
                .address(requestDTO.getAddress())
                .phone(requestDTO.getPhone())
                .email(requestDTO.getEmail())
                .logoUrl(requestDTO.getLogoUrl())
                .isActive(true)
                .build();

        // 3. توليد الكود التجاري (Business Code)
        String businessCode = "COMP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        company.setBusinessCode(businessCode);

        // 4. حفظ الشركة
        Company savedCompany = companyRepository.save(company);
        log.info("تم إنشاء الشركة بنجاح بالكود: {}", businessCode);

        // 5. إرجاع الـ ResponseDTO
        return mapToResponseDTO(savedCompany);
    }

    // ===========================================
    // البحث عن شركه بالكود
    // ===========================================
    @Override
    @Transactional(readOnly = true)
    public CompanyResponseDTO getCompanyByBusinessCode(String businessCode) {
        log.debug("البحث عن شركة بالكود: {}", businessCode);

        Company company = companyRepository.findByBusinessCode(businessCode)
                .orElseThrow(() -> {
                    log.error("الشركة غير موجودة بالكود: {}", businessCode);
                    return new ResourceNotFoundException("الشركة غير موجودة بالكود: " + businessCode);
                });

        return mapToResponseDTO(company);
    }

    // ===========================================
    // جلب جميع الشركات
    // ===========================================
    @Override
    @Transactional(readOnly = true)
    public List<CompanyResponseDTO> getAllCompanies() {
        log.debug("جلب جميع الشركات");
        return companyRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    // ===========================================
    // 🆕 جلب الشركات المعطلة
    // ===========================================
    @Override
    @Transactional(readOnly = true)
    public List<CompanyResponseDTO> getInactiveCompanies() {
        log.debug("جلب الشركات المعطلة");
        return companyRepository.findByIsActiveFalse()
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    // ===========================================
    // 🆕 جلب الشركات النشطة
    // ===========================================
    @Override
    @Transactional(readOnly = true)
    public List<CompanyResponseDTO> getActiveCompanies() {
        log.debug("جلب الشركات النشطة");
        return companyRepository.findByIsActiveTrue()
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    // ===========================================
    // تعديل بيانات شركه
    // ===========================================
    @Override
    public CompanyResponseDTO updateCompany(String businessCode, CompanyRequestDTO requestDTO) {
        log.info("تعديل بيانات الشركة بالكود: {}", businessCode);

        Company company = companyRepository.findByBusinessCode(businessCode)
                .orElseThrow(() -> {
                    log.error("الشركة غير موجودة بالكود: {}", businessCode);
                    return new ResourceNotFoundException("الشركة غير موجودة بالكود: " + businessCode);
                });

        // إذا تغيّر الرقم الضريبي، نتحقق من أنه غير مستخدم بشركة أخرى
        if (!company.getTaxNumber().equals(requestDTO.getTaxNumber()) &&
                companyRepository.existsByTaxNumber(requestDTO.getTaxNumber())) {
            log.warn("محاولة استخدام رقم ضريبي مستخدم مسبقاً: {}", requestDTO.getTaxNumber());
            throw new DuplicateResourceException("الرقم الضريبي الجديد مُسجّل مسبقاً لشركة أخرى");
        }

        company.setNameAr(requestDTO.getNameAr());
        company.setNameEn(requestDTO.getNameEn());
        company.setTaxNumber(requestDTO.getTaxNumber());
        company.setAddress(requestDTO.getAddress());
        company.setPhone(requestDTO.getPhone());
        company.setEmail(requestDTO.getEmail());
        company.setLogoUrl(requestDTO.getLogoUrl());

        Company updatedCompany = companyRepository.save(company);
        log.info("تم تحديث الشركة بنجاح بالكود: {}", businessCode);

        return mapToResponseDTO(updatedCompany);
    }

    // ===========================================
    // تعطيل عمل شركه (موجود مسبقاً)
    // ===========================================
    @Override
    public void disableCompany(String businessCode) {
        log.info("تعطيل الشركة بالكود: {}", businessCode);

        Company company = companyRepository.findByBusinessCode(businessCode)
                .orElseThrow(() -> {
                    log.error("الشركة غير موجودة بالكود: {}", businessCode);
                    return new ResourceNotFoundException("الشركة غير موجودة بالكود: " + businessCode);
                });

        // التحقق من أن الشركة ليست معطلة بالفعل
        if (!company.isActive()) {
            log.warn("الشركة بالكود {} معطلة بالفعل", businessCode);
            throw new IllegalStateException("الشركة معطلة بالفعل");
        }

        company.setActive(false);
        companyRepository.save(company);
        log.info("تم تعطيل الشركة بنجاح بالكود: {}", businessCode);
    }

    // ===========================================
    // 🆕 إعادة تفعيل الشركة
    // ===========================================
    @Override
    public CompanyResponseDTO reactivateCompany(String businessCode) {
        log.info("إعادة تفعيل الشركة بالكود: {}", businessCode);

        Company company = companyRepository.findByBusinessCode(businessCode)
                .orElseThrow(() -> {
                    log.error("الشركة غير موجودة بالكود: {}", businessCode);
                    return new ResourceNotFoundException("الشركة غير موجودة بالكود: " + businessCode);
                });

        // التحقق من أن الشركة معطلة
        if (company.isActive()) {
            log.warn("الشركة بالكود {} نشطة بالفعل", businessCode);
            throw new IllegalStateException("الشركة نشطة بالفعل، لا حاجة لإعادة التفعيل");
        }

        company.setActive(true);
        Company reactivatedCompany = companyRepository.save(company);
        log.info("تم إعادة تفعيل الشركة بنجاح بالكود: {}", businessCode);

        return mapToResponseDTO(reactivatedCompany);
    }

    // ===========================================
    // 🆕 حذف شركة نهائياً
    // ===========================================
    @Override
    public void deleteCompany(String businessCode) {
        log.warn("⚠️ محاولة حذف شركة نهائياً بالكود: {}", businessCode);

        // التحقق من وجود الشركة
        if (!companyRepository.existsByBusinessCode(businessCode)) {
            log.error("الشركة غير موجودة بالكود: {}", businessCode);
            throw new ResourceNotFoundException("الشركة غير موجودة بالكود: " + businessCode);
        }

        // TODO: هنا يمكنك إضافة التحقق من عدم وجود حسابات أو قيود مرتبطة بالشركة
        // مثال: if (accountRepository.existsByCompanyBusinessCode(businessCode)) {
        //     throw new CompanyHasDependenciesException("لا يمكن حذف الشركة لأنها مرتبطة بحسابات");
        // }

        // حذف الشركة
        companyRepository.deleteByBusinessCode(businessCode);
        log.warn("✅ تم حذف الشركة نهائياً بالكود: {}", businessCode);
    }

    // ===========================================
    // 🆕 حذف جميع الشركات نهائياً
    // ===========================================
    @Override
    public void deleteAllCompanies() {
        log.warn("⚠️⚠️⚠️ محاولة حذف جميع الشركات نهائياً!");

        // TODO: هنا يمكنك إضافة التحقق من الصلاحيات
        // مثال: إذا كان المستخدم ليس Admin، ارمي استثناء

        // TODO: التحقق من عدم وجود بيانات مرتبطة بالشركات

        companyRepository.deleteAllCompanies();
        log.warn("✅ تم حذف جميع الشركات نهائياً!");
    }

    // ===========================================
    // 🆕 التحقق من وجود شركة
    // ===========================================
    @Override
    public boolean existsByBusinessCode(String businessCode) {
        return companyRepository.existsByBusinessCode(businessCode);
    }

    // =========================================================
    // Helper Methods
    // =========================================================

    private CompanyResponseDTO mapToResponseDTO(Company company) {
        if (company == null) {
            return null;
        }

        CompanyResponseDTO dto = new CompanyResponseDTO();
        dto.setBusinessCode(company.getBusinessCode());
        dto.setId(company.getId());
        dto.setNameAr(company.getNameAr());
        dto.setNameEn(company.getNameEn());
        dto.setTaxNumber(company.getTaxNumber());
        dto.setAddress(company.getAddress());
        dto.setPhone(company.getPhone());
        dto.setEmail(company.getEmail());
        dto.setLogoUrl(company.getLogoUrl());
        dto.setActive(company.isActive());
        dto.setCreatedAt(company.getCreatedAt());

        return dto;
    }
}