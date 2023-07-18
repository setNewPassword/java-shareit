package ru.practicum.shareit.exception;

public class BookingNotFoundException extends EntityNotFoundException {
    public BookingNotFoundException(String message) {
        super(message);
    }
}