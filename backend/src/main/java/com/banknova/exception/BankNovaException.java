package com.banknova.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class BankNovaException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String errorCode;

    public BankNovaException(String message) {
        this(message, HttpStatus.BAD_REQUEST, "BANKNOVA_ERROR");
    }

    public BankNovaException(String message, HttpStatus httpStatus, String errorCode) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }

    public BankNovaException(String message, Throwable cause, HttpStatus httpStatus, String errorCode) {
        super(message, cause);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }
}
