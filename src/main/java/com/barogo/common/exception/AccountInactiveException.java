package com.barogo.common.exception;

/**
 * 휴면계정 exception
 * @author ehjang
 */
public class AccountInactiveException extends RuntimeException {
    public AccountInactiveException(String message) { super(message); }
}
