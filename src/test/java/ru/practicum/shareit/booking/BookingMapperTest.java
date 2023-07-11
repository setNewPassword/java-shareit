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
import static org.junit.jupiter.api.Assertions.assertNull;

public class BookingMapperTest {
    private Booking booking;
    private BookingShortDto bookingShortDto;
    private BookingDto bookingDto;
    private final BookingMapper bookingMapper = Mappers.getMapper(BookingMapper.class);

    @BeforeEach
    public void beforeEach() {
        booking = Booking
                .builder()
                .id(1L)
                .start(LocalDateTime.parse("2023-07-07T12:30:01.35"))
                .end(LocalDateTime.parse("2023-07-10T15:35:10.15"))
                .item(new Item())
                .booker(new User())
                .build();

        bookingShortDto = BookingShortDto
                .builder()
                .id(1L)
                .start(LocalDateTime.parse("2023-07-07T12:30:01.35"))
                .end(LocalDateTime.parse("2023-07-10T15:35:10.15"))
                .build();
        bookingDto = BookingDto
                .builder()
                .id(1L)
                .start(LocalDateTime.parse("2023-07-07T12:30:01.35"))
                .end(LocalDateTime.parse("2023-07-10T15:35:10.15"))
                .build();
    }

    @Test
    public void toBookingDtoTest() {
        BookingDto dto = bookingMapper.toDto(booking);

        assertEquals(dto.getId(), booking.getId());
        assertEquals(dto.getStart(), booking.getStart());
        assertEquals(dto.getEnd(), booking.getEnd());
    }

    @Test
    public void nullToBookingDtoTest() {
        BookingDto dto = bookingMapper.toDto(null);

        assertNull(dto);
    }

    @Test
    public void bookingShortDtoTest() {
        BookingShortDto dto = BookingMapper.toShortDto(booking);

        assertEquals(dto.getId(), booking.getId());
        assertEquals(dto.getStart(), booking.getStart());
        assertEquals(dto.getEnd(), booking.getEnd());
    }


    @Test
    public void shortToBookingTest() {
        Booking newBooking = bookingMapper.toEntity(bookingShortDto);

        assertEquals(newBooking.getStart(), bookingShortDto.getStart());
        assertEquals(newBooking.getEnd(), bookingShortDto.getEnd());
    }

    @Test
    public void nullShortToBookingTest() {
        bookingShortDto = null;
        Booking newBooking = bookingMapper.toEntity(bookingShortDto);

        assertNull(newBooking);
    }

    @Test
    public void fullToBookingTest() {
        Booking newBooking = bookingMapper.toEntity(bookingDto);

        assertEquals(newBooking.getStart(), bookingDto.getStart());
        assertEquals(newBooking.getEnd(), bookingDto.getEnd());
    }

    @Test
    public void nullFullToBookingTest() {
        bookingDto = null;
        Booking newBooking = bookingMapper.toEntity(bookingDto);

        assertNull(newBooking);
    }
}
