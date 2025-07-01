package com.barogo.common.exception;

/**
 * token 관련 오류 exception
 * @author ehjang
 */
public class TokenStorageException extends RuntimeException {
    public TokenStorageException(String message) { super(message); }
}
