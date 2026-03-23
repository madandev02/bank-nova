package com.banknova.exception;

import org.springframework.http.HttpStatus;

public class InsufficientBalanceException extends BankNovaException {

    public InsufficientBalanceException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "INSUFFICIENT_BALANCE");
    }
}
