package ru.practicum.shareit.booking;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;

@Mapper(componentModel = "spring")
@Service
public interface BookingMapper {
    BookingDto toDto(Booking booking);

    @Named("bookingToBookingShortDto")
    public static BookingShortDto toShortDto(Booking booking) {
        return BookingShortDto
                .builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .itemId(booking.getItem().getId())
                .bookerId(booking.getBooker().getId())
                .build();
    }

    ;

    Booking toEntity(BookingDto bookingDto);

    Booking toEntity(BookingShortDto bookingShortDto);

}