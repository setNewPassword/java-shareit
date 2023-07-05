package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookingMapperTest {
    private Booking booking;
    private BookingShortDto bookingShortDto;
    private final BookingMapper bookingMapper = Mappers.getMapper(BookingMapper.class);

    @BeforeEach
    public void beforeEach() {
        booking = new Booking(1L,
                LocalDateTime.parse("2023-07-07T12:30:01.35"),
                LocalDateTime.parse("2023-07-10T15:35:10.15"),
                new Item(),
                new User(),
                null);

        bookingShortDto = new BookingShortDto();
        bookingShortDto.setId(1L);
        bookingShortDto.setStart(LocalDateTime.parse("2023-07-07T12:30:01.35"));
        bookingShortDto.setEnd(LocalDateTime.parse("2023-07-10T15:35:10.15"));
    }

    @Test
    public void toBookingDtoTest() {
        BookingDto dto = bookingMapper.toDto(booking);

        assertEquals(dto.getId(), booking.getId());
        assertEquals(dto.getStart(), booking.getStart());
        assertEquals(dto.getEnd(), booking.getEnd());
    }

    @Test
    public void bookingShortDtoTest() {
        BookingShortDto dto = BookingMapper.toShortDto(booking);

        assertEquals(dto.getId(), booking.getId());
        assertEquals(dto.getStart(), booking.getStart());
        assertEquals(dto.getEnd(), booking.getEnd());
    }

    @Test
    public void toBookingTest() {
        Booking newBooking = bookingMapper.toEntity(bookingShortDto);

        assertEquals(newBooking.getStart(), bookingShortDto.getStart());
        assertEquals(newBooking.getEnd(), bookingShortDto.getEnd());
    }
}
