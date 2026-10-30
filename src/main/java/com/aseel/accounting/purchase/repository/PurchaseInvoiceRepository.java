//package com.aseel.accounting.purchase.repository;
//
//import com.aseel.accounting.purchase.model.PurchaseInvoice;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
//@Repository
//public interface PurchaseInvoiceRepository extends JpaRepository<PurchaseInvoice, Long> {
//    List<PurchaseInvoice> findByCompanyBusinessCode(String companyBusinessCode);
//
//    // استعلام لجلب آخر رقم فاتورة مسجل لنفس الشركة لكي نقوم بتوليد الرقم التالي تلقائياً
//    @Query("SELECT p.invoiceNumber FROM PurchaseInvoice p WHERE p.companyBusinessCode = :companyCode ORDER BY p.id DESC LIMIT 1")
//    String findLastInvoiceNumber(String companyCode);
//}


package com.aseel.accounting.purchase.repository;

import com.aseel.accounting.purchase.model.PurchaseInvoice;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseInvoiceRepository extends JpaRepository<PurchaseInvoice, Long> {

    List<PurchaseInvoice> findByCompanyBusinessCode(String companyBusinessCode);

    // استعلام لجلب آخر رقم فاتورة مسجل لنفس الشركة باستخدام Pageable
    @Query("SELECT p.invoiceNumber FROM PurchaseInvoice p WHERE p.companyBusinessCode = :companyCode ORDER BY p.id DESC")
    List<String> findLastInvoiceNumbersByCompany(@Param("companyCode") String companyCode, Pageable pageable);
}
