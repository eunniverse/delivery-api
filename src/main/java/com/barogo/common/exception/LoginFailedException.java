package com.barogo.common.exception;

/**
 * 로그인 실패 exception
 * @author ehjang
 */
public class LoginFailedException extends RuntimeException {
    public LoginFailedException(String message) { super(message); }
}
