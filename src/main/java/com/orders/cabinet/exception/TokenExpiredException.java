package com.orders.cabinet.exception;

/**
 * demo
 * Author: Vasylenko Oleksii
 * Date: 29.05.2024
 */
public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException(String message) {
        super(message);
    }
}
