package ru.practicum.shareit.booking.model;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;

public enum Status {
    WAITING,
    APPROVED,
    REJECTED,
    CANCELED
}
