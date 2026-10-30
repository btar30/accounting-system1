package com.aseel.accounting.user.repository;

import com.aseel.accounting.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByBusinessCode(String businessCode);

    boolean existsByUsername(String username);

    // استعراض مستخدمي شركة محددة
    List<User> findByCompanyBusinessCode(String companyBusinessCode);

    // استعراض حسب الشركة والدور (إذا لزم التصفية بالدور أيضاً)
    List<User> findByCompanyBusinessCodeAndRole(String companyBusinessCode, com.aseel.accounting.user.model.Role role);

    boolean existsByRole(String role);

}
