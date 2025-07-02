package com.barogo.common.exception;

/**
 * token 관련 exception
 * @author ehjang
 */
public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String message) {
        super(message);
    }
}