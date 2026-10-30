package com.aseel.accounting.sale.repository;

import com.aseel.accounting.sale.model.SalesInvoice;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalesInvoiceRepository extends JpaRepository<SalesInvoice, Long> {
    List<SalesInvoice> findByCompanyBusinessCode(String companyBusinessCode);

    @Query("SELECT s.invoiceNumber FROM SalesInvoice s WHERE s.companyBusinessCode = :companyCode ORDER BY s.id DESC")
    List<String> findLastInvoiceNumbersByCompany(@Param("companyCode") String companyCode, Pageable pageable);
}
