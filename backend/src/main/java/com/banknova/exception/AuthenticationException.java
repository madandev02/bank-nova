package com.banknova.exception;

import org.springframework.http.HttpStatus;

public class AuthenticationException extends BankNovaException {

    public AuthenticationException(String message) {
        super(message, HttpStatus.UNAUTHORIZED, "AUTHENTICATION_FAILED");
    }
}
