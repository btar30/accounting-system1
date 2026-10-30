package com.aseel.accounting.user.controller;

import com.aseel.accounting.user.dto.RegisterRequestDTO;
import com.aseel.accounting.user.dto.UserResponseDTO;
import com.aseel.accounting.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // 📌 حقن الـ UserService للتعامل مع العمليات المنطقية
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // =========================================================================
    // 1. تسجيل مستخدم جديد
    // =========================================================================
    // يستقبل بيانات المستخدم وصلاحية المستخدم الحالي (عبر الـ Authentication)
    // لضمان أن مدير الشركة لا يُسجل إلا لشركته، والمدير العام يختار أي شركة.
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(
            @RequestBody RegisterRequestDTO dto,
            Authentication authentication) {

        // استخراج اسم المستخدم الحالي من توكن الـ JWT المسجل دخولاً
        String currentUsername = authentication.getName();
        userService.registerUser(dto, currentUsername);

        return ResponseEntity.ok("تم إنشاء المستخدم بنجاح وتوليد كوده تلقائياً");
    }

    // =========================================================================
    // 2. جلب قائمة المستخدمين
    // =========================================================================
    // المدير العام يرى جميع مستخدمي النظام.
    // مدير الشركة يرى فقط مستخدمي شركته المرتبطة به.
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers(Authentication authentication) {
        String currentUsername = authentication.getName();
        List<UserResponseDTO> users = userService.getUsersByCurrentAdmin(currentUsername);
        return ResponseEntity.ok(users);
    }

    // =========================================================================
    // 3. تعديل بيانات مستخدم
    // =========================================================================
    // يتم التعديل باستخدام الكود التجاري للمستخدم (businessCode) مع التحقق من الصلاحيات.
    @PutMapping("/{businessCode}")
    public ResponseEntity<String> updateUser(
            @PathVariable String businessCode,
            @RequestBody RegisterRequestDTO dto,
            Authentication authentication) {

        userService.updateUser(businessCode, dto, authentication.getName());
        return ResponseEntity.ok("تم تحديث بيانات المستخدم بنجاح");
    }

    // =========================================================================
    // 4. إيقاف أو تفعيل حساب مستخدم (Toggle Status)
    // =========================================================================
    @PatchMapping("/{businessCode}/toggle-status")
    public ResponseEntity<String> toggleStatus(
            @PathVariable String businessCode,
            Authentication authentication) {

        userService.toggleUserStatus(businessCode, authentication.getName());
        return ResponseEntity.ok("تم تغيير حالة المستخدم بنجاح");
    }

    // =========================================================================
    // 5. حذف مستخدم نهائياً
    // =========================================================================
    @DeleteMapping("/{businessCode}")
    public ResponseEntity<String> deleteUser(
            @PathVariable String businessCode,
            Authentication authentication) {

        userService.deleteUser(businessCode, authentication.getName());
        return ResponseEntity.ok("تم حذف المستخدم بنجاح");
    }
}
