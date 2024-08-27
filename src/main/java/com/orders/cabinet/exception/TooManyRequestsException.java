package com.orders.cabinet.exception;

/**
 * demo
 * Author: Vasylenko Oleksii
 * Date: 29.05.2024
 */
public class TooManyRequestsException extends RuntimeException{
    public TooManyRequestsException(String message) {
        super(message);
    }
}
