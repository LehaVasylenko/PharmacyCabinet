package com.orders.cabinet.exception;

/**
 * demo
 * Author: Vasylenko Oleksii
 * Date: 29.05.2024
 */
public class NoSuchShopException extends RuntimeException{
    public NoSuchShopException(String message) {
        super(message);
    }

    public NoSuchShopException(StringBuilder message) {
        super(message.toString());
    }
}
