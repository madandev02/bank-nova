package com.banknova.exception;

import org.springframework.http.HttpStatus;

public class DuplicateEmailException extends BankNovaException {

    public DuplicateEmailException(String email) {
        super("Email already exists: " + email, HttpStatus.CONFLICT, "DUPLICATE_EMAIL");
    }
}
