package ru.practicum.shareit.exception;

public abstract class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String message) {
        super(message);
    }
}
