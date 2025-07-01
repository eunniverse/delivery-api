package com.barogo.common.exception;

/**
 * refresh token 오류 exception
 * @author ehjang
 */
public class InvalidRefreshTokenException extends RuntimeException {
    public InvalidRefreshTokenException(String message) {
        super(message);
    }
}
