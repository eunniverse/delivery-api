package com.barogo.common.exception;

/**
 * refresh token 만료 exception
 * @author ehjang
 */
public class ExpiredRefreshTokenException extends RuntimeException {
    public ExpiredRefreshTokenException(String message) {
        super(message);
    }
}
