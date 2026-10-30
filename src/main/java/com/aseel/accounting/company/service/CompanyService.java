package com.aseel.accounting.company.service;

import com.aseel.accounting.company.dto.request.CompanyRequestDTO;
import com.aseel.accounting.company.dto.response.CompanyResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CompanyService {

    /**
     * إنشاء شركة جديدة.
     */
    CompanyResponseDTO createCompany(CompanyRequestDTO requestDTO);

    /**
     * جلب شركة بواسطة الكود التجاري.
     */
    CompanyResponseDTO getCompanyByBusinessCode(String businessCode);

    /**
     * جلب قائمة جميع الشركات دون تقسيم صفحات.
     */
    List<CompanyResponseDTO> getAllCompanies();

    /**
     * 🆕 جلب جميع الشركات المعطلة (غير النشطة).
     */
    List<CompanyResponseDTO> getInactiveCompanies();

    /**
     * 🆕 جلب جميع الشركات النشطة.
     */
    List<CompanyResponseDTO> getActiveCompanies();

    /**
     * تعديل بيانات الشركة.
     */
    CompanyResponseDTO updateCompany(String businessCode, CompanyRequestDTO requestDTO);

    /**
     * تعطيل الشركة (Soft Delete) - موجودة بالفعل.
     */
    void disableCompany(String businessCode);

    /**
     * 🆕 إعادة تفعيل الشركة (Reactivate).
     */
    CompanyResponseDTO reactivateCompany(String businessCode);

    /**
     * 🆕 حذف شركة نهائياً (Permanent Delete).
     */
    void deleteCompany(String businessCode);


    /**
     * 🆕 حذف جميع الشركات نهائياً (Delete All).
     * تحذير: هذه العملية خطيرة جداً!
     */
    void deleteAllCompanies();


    /**
     * 🆕 التحقق من وجود شركة بالكود التجاري.
     */
    boolean existsByBusinessCode(String businessCode);
}