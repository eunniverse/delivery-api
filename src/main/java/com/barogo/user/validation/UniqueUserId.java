package com.barogo.user.validation;

import jakarta.validation.Constraint;
import java.lang.annotation.*;
import jakarta.validation.Payload;

/**
 * ID 중복 유효성검사
 * @author ehjang
 */
@Constraint(validatedBy = UniqueUserIdValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueUserId {
    String message() default "이미 존재하는 아이디입니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
