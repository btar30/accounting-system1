package com.aseel.accounting.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * استثناء عام يستخدم عندما لا يتم العثور على سجل في قاعدة البيانات.
 * يقوم تلقائياً بإرجاع حالة الرد 404 Not Found.
 */
@ResponseStatus(HttpStatus.NOT_FOUND) // قرار معماري: تحديد حالة الرد تلقائياً
public class ResourceNotFoundException extends RuntimeException {

    // Constructors متعددة لسهولة الاستخدام

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
