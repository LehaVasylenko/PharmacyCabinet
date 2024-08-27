package com.orders.cabinet.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CustomErrorResponse {
    @JsonProperty("error_message")
    private final String errorMessage;
}