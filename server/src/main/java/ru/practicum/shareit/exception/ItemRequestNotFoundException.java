package ru.practicum.shareit.exception;

public class ItemRequestNotFoundException extends EntityNotFoundException {
    public ItemRequestNotFoundException(String message) {
        super(message);
    }
}