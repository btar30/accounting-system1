//package com.aseel.accounting.common.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.domain.AuditorAware;
//import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
//
//import java.util.Optional;
//
//
////**
//// * إعداد Auditing لتسجيل المستخدم الذي قام بالإنشاء أو التعديل.
//// **
//@Configuration
//@EnableJpaAuditing // تفعيل Auditing في سياق JPA
//public class AuditingConfig {
//
//    @Bean
//    public AuditorAware<String> auditorProvider() {
//        // في هذه المرحلة (قبل Security)، سنستخدم مستخدمًا افتراضيًا باسم "SYSTEM"
//        // لاحقًا، سنقوم بتعديل هذا الجزء ليقرأ المستخدم الفعلي من SecurityContext
//        return () -> Optional.of("SYSTEM");
//    }
//}
//


package com.aseel.accounting.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class AuditingConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() ||
                    authentication.getPrincipal().equals("anonymousUser")) {
                return Optional.of("SYSTEM");
            }
            return Optional.of(authentication.getName());
        };
    }
}
