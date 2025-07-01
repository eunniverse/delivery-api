package com.barogo.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Map;
import org.springframework.validation.FieldError;

/**
 * 모든 exception 처리
 * @author ehjang
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAllExceptions(Exception ex) {
        log.error("Unhandled exception: ", ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요."));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationErrors(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .filter(msg -> msg != null && !msg.isBlank())
                .findFirst()
                .orElse("입력값이 올바르지 않습니다.");

        return ResponseEntity
                .badRequest()
                .body(Map.of("message", message));
    }

    @ExceptionHandler(LoginFailedException.class)
    public ResponseEntity<Map<String, String>> handleLoginFailed(LoginFailedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(AccountInactiveException.class)
    public ResponseEntity<Map<String, String>> handleAccountInactive(AccountInactiveException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(TokenStorageException.class)
    public ResponseEntity<Map<String, String>> handleTokenStorage(TokenStorageException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<?> handleInvalidToken(InvalidRefreshTokenException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(ExpiredRefreshTokenException.class)
    public ResponseEntity<?> handleExpiredToken(ExpiredRefreshTokenException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", ex.getMessage()));
    }
}

