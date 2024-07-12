package com.orders.cabinet.exception;

/**
 * demo
 * Author: Vasylenko Oleksii
 * Date: 29.05.2024
 */
public class OrderOutOfDateException extends RuntimeException {
    public OrderOutOfDateException(String message) {
        super(message);
    }
}
