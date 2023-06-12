package ru.practicum.shareit.exception;

public class ItemNotFoundException extends EntityNotFoundException {

    public ItemNotFoundException(String message) {
        super(message);
    }
}
