package com.aseel.accounting;

import com.aseel.accounting.user.model.Role;
import com.aseel.accounting.user.model.User;
import com.aseel.accounting.user.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@SpringBootApplication
public class AccountingApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountingApplication.class, args);
    }

    @Bean
    CommandLineRunner initDefaultUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            String adminUsername = "admin";
            Optional<User> existingAdmin = userRepository.findByUsername(adminUsername);

            User admin;
            if (existingAdmin.isPresent()) {
                admin = existingAdmin.get();
                System.out.println("🔄 حساب المدير موجود مسبقاً، جاري تحديث بياناته وصلاحياته...");
            } else {
                admin = new User();
                admin.setUsername(adminUsername);
                admin.setBusinessCode("USR-ADMIN-01");
                System.out.println("✨ جاري إنشاء حساب المدير الافتراضي الجديد...");
            }

            admin.setPassword(passwordEncoder.encode("123456"));
            admin.setFullName("مدير النظام الرئيسي");
            admin.setRole(Role.ADMIN_SYSTEM);
            admin.setCompanyBusinessCode("COMP-917638C0");
            admin.setActive(true);

            userRepository.save(admin);
            System.out.println("✅ تم ضبط حساب مدير النظام بنجاح (اسم المستخدم: admin / كلمة المرور: 123456)");
        };
    }

}
