package com.aseel.accounting.item.repository;

import com.aseel.accounting.item.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    // جلب كافة الأصناف التابعة لشركة معينة باستخدام كود الشركة
    List<Item> findByCompanyBusinessCode(String companyBusinessCode);

    // البحث عن صنف معين داخل شركة معينة باستخدام كود الصنف وكود الشركة
    Optional<Item> findByCompanyBusinessCodeAndItemCode(String companyBusinessCode, String itemCode);
}
