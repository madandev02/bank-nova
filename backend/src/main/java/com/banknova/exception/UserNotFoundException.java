package com.banknova.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BankNovaException {

    public UserNotFoundException(String email) {
        super("User not found with email: " + email, HttpStatus.NOT_FOUND, "USER_NOT_FOUND");
    }

    public UserNotFoundException(Long userId) {
        super("User not found with ID: " + userId, HttpStatus.NOT_FOUND, "USER_NOT_FOUND");
    }
}
