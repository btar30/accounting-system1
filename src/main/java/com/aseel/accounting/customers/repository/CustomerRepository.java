package com.aseel.accounting.customers.repository;

import com.aseel.accounting.customers.model.Customer;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    List<Customer> findByCompanyBusinessCode(String companyBusinessCode);

    Optional<Customer> findByCompanyBusinessCodeAndCustomerCode(String companyBusinessCode, String customerCode);

    // ===== باستخدام Pageable (متوافق مع جميع قواعد البيانات) =====
    @Query("SELECT c.customerCode FROM Customer c WHERE c.companyBusinessCode = :companyCode ORDER BY c.id DESC")
    List<String> findLastCustomerCodeByCompany(@Param("companyCode") String companyCode, Pageable pageable);
}