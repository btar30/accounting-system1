package com.aseel.accounting.company.repository;

import com.aseel.accounting.company.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * مستودع بيانات الشركة.
 * يوفر العمليات الأساسية (CRUD) تلقائياً عبر JpaRepository.
 */
@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    /**
     * البحث عن شركة بواسطة الكود التجاري (قرارنا المعماري الذهبي).
     */
    Optional<Company> findByBusinessCode(String businessCode);

    /**
     * التحقق من وجود شركة بنفس الرَّقَم الضريبي (لمنع التكرار).
     */
    boolean existsByTaxNumber(String taxNumber);

    /**
     * البحث عن جميع الشركات المعطلة (غير النشطة).
     */
    List<Company> findByIsActiveFalse();

    /**
     * البحث عن جميع الشركات النشطة.
     */
    List<Company> findByIsActiveTrue();

    /**
     * حذف شركة بواسطة الكود التجاري (حذف نهائي).
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM Company c WHERE c.businessCode = :businessCode")
    void deleteByBusinessCode(@Param("businessCode") String businessCode);

    /**
     * التحقق من وجود شركة بالكود التجاري.
     */
    boolean existsByBusinessCode(String businessCode);

    /**
     * حذف جميع الشركات (حذف نهائي).
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM Company")
    void deleteAllCompanies();
}