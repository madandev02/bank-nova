package com.banknova.exception;

import org.springframework.http.HttpStatus;

public class InvalidTransactionException extends BankNovaException {

    public InvalidTransactionException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "INVALID_TRANSACTION");
    }
}
