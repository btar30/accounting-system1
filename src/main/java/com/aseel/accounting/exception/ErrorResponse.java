package com.aseel.accounting.exception;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {

    private int status;            // كود الحالة (مثلاً 400, 404, 500)
    private String error;          // وصف نوع الخطأ (مثل Bad Request)
    private String message;        // رسالة الخطأ الواضحة للمستخدم
    private LocalDateTime timestamp; // وقت حدوث الخطأ
    private Map<String, String> validationErrors; // أخطاء الـ Validation إن وجدت (@Valid)
}
