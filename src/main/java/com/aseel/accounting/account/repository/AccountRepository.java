package com.aseel.accounting.account.repository;

import com.aseel.accounting.account.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    // 1. جلب جميع حسابات شركة معينة
    List<Account> findByCompanyId(Long companyId);


    // 2. جلب الحسابات الرئيسية من المستوى الأول (التي لا يتبع لها أي حساب أب) لشركة معينة
    List<Account> findByCompanyIdAndParentAccountIsNull(Long companyId);

    // 3. التثبت من وجود رمز حساب مكرر لنفس الشركة
    boolean existsByCompanyIdAndAccountCode(Long companyId, String accountCode);

    // 4. الحصول على أكبر رمز حساب للحسابات الرئيسية (Root) التي لا تملك حسابًا أبًا
    @Query("SELECT MAX(a.accountCode) FROM Account a WHERE a.company.id = :companyId AND a.parentAccount IS NULL")
    Optional<String> findMaxAccountCodeForRootByCompanyId(@Param("companyId") Long companyId);

    // 5. الحصول على أكبر رمز حساب (Max Account Code) بين الحسابات الأبناء لحساب أبُّ معين
    // يفيدنا جدًا في الـ Service لتوليد الرمز التلقائي للحساب الجديد
    @Query("SELECT MAX(a.accountCode) FROM Account a WHERE a.company.id = :companyId AND a.parentAccount.id = :parentId")
    Optional<String> findMaxAccountCodeByCompanyIdAndParentAccountId(@Param("companyId") Long companyId, @Param("parentId") Long parentId);


    //========================================================
    //جلب حسابات شركه عن طريق ود ألشركه
    List<Account> findByCompanyBusinessCode(String businessCode);
    //جلب جميع الحسابات
    List<Account> findByCompanyBusinessCodeAndParentAccountIsNull(String businessCode);
    //===========================================================
}
