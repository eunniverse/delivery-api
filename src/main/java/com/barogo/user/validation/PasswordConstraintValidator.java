package com.barogo.user.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 패스워드 유효성 검사
 * @author ehjang
 */
public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public void initialize(ValidPassword constraintAnnotation) {}

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null || password.length() < 12) return false;

        int count = 0;
        if (password.matches(".*[A-Z].*")) count++; // 대문자
        if (password.matches(".*[a-z].*")) count++; // 소문자
        if (password.matches(".*[0-9].*")) count++; // 숫자
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) count++; // 특수문자

        return count >= 3;
    }
}
