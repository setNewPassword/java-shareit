package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.State;

import java.util.List;

public interface BookingService {
    BookingDto create(BookingShortDto bookingShortDto, Long userId);

    BookingDto approve(Long bookingId, Long userId, Boolean approved);

    List<BookingDto> getAllByOwner(Long ownerId, State state, int from, int size);

    List<BookingDto> getAllByUser(Long bookerId, State state, int from, int size);

    BookingDto getById(Long bookingId, Long userId);
}