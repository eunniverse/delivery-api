package com.barogo.user.validation;

import com.barogo.user.repository.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UniqueUserIdValidator implements ConstraintValidator<UniqueUserId, String> {

    private final UserRepository userRepository;

    @Override
    public boolean isValid(String userId, ConstraintValidatorContext context) {
        if (userId == null || userId.trim().isEmpty()) {
            return false;
        }

        return !userRepository.existsByUserId(userId);
    }
}
