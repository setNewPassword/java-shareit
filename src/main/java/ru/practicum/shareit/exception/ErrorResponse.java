package ru.practicum.shareit.exception;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ErrorResponse {
    private final String message;
    private final LocalDateTime timestamp;

    public ErrorResponse(String message) {
        this.message = message;
        timestamp = LocalDateTime.now();
    }
}
