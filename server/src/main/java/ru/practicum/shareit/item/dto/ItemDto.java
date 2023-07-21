package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Value;
import ru.practicum.shareit.booking.dto.BookingShortDto;

import java.util.List;

@Value
@Builder(toBuilder = true)
public class ItemDto {

    long id;

    String name;

    String description;

    Boolean available;

    BookingShortDto lastBooking;

    BookingShortDto nextBooking;

    List<CommentDto> comments;

    Long requestId;
}