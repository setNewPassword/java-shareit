package ru.practicum.shareit.exception;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ErrorResponse {
    private final String error;
    private final LocalDateTime timestamp;

    public ErrorResponse(String error) {
        this.error = error;
        timestamp = LocalDateTime.now();
    }
}