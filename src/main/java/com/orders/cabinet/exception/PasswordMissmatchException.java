package com.orders.cabinet.exception;

public class PasswordMissmatchException extends RuntimeException{
    public PasswordMissmatchException(String message) {
        super(message);
    }
}
