package com.aseel.accounting.supplier.repository;

import com.aseel.accounting.supplier.model.Supplier;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    List<Supplier> findByCompanyBusinessCode(String companyBusinessCode);

    Optional<Supplier> findByCompanyBusinessCodeAndSupplierCode(String companyBusinessCode, String supplierCode);

    // ===== باستخدام Pageable (متوافق مع جميع قواعد البيانات) =====
    @Query("SELECT s.supplierCode FROM Supplier s WHERE s.companyBusinessCode = :companyCode ORDER BY s.id DESC")
    List<String> findLastSupplierCodeByCompany(@Param("companyCode") String companyCode, Pageable pageable);
}