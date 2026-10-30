package com.aseel.accounting.user.controller;

import com.aseel.accounting.user.dto.AuthResponseDTO;
import com.aseel.accounting.user.dto.LoginRequestDTO;
import com.aseel.accounting.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 🔐 كنترولر المصادقة (Authentication Controller)
 * مسؤول عن استقبال طلبات تسجيل الدخول للعامة (Public Endpoints)
 * دون الحاجة لتوكن مسبق، وتوجيهها للخدمة المختصة.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    // 📌 حقن الـ UserService للتعامل مع منطق المصادقة والتحقق من الشركات
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // =========================================================================
    // 1. تسجيل الدخول (Login Endpoint)
    // =========================================================================
    /**
     * يستقبل بيانات الاعتماد (اسم المستخدم وكلمة المرور)، ويتحقق من:
     * - صحة بيانات الدخول.
     * - حالة حساب المستخدم (هل هو مفعّل؟).
     * - حالة الشركة المرتبطة بالمستخدم (هل شركته نشطة وغير معطلة من قِبل الإدارة؟).
     *
     * @param requestDTO يحتوي على username و password
     * @return استجابة تحتوي على الـ JWT Token وبيانات المستخدم وكود الشركة النصي
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO requestDTO) {
        AuthResponseDTO response = userService.login(requestDTO);
        return ResponseEntity.ok(response);
    }
}
