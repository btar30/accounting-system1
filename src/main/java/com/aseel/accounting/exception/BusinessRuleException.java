package com.aseel.accounting.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * استثناء يُرمى عند مخالفة القواعد والضوابط المحاسبية (مثل عدم توازن القيد أو القيد على حساب رئيسي).
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
