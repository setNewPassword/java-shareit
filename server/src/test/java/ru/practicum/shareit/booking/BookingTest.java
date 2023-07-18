package ru.practicum.shareit.booking;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingShortDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingTest {
    @Autowired
    JacksonTester<BookingShortDto> json;

    @Test
    @SneakyThrows
    void bookingShortDtoJsonTest() {
        BookingShortDto bookingShortDto = BookingShortDto
                .builder()
                .id(1L)
                .start(LocalDateTime.parse("2023-07-07T12:30:01.35"))
                .end(LocalDateTime.parse("2023-07-10T15:35:10.15"))
                .build();
        JsonContent<BookingShortDto> result = json.write(bookingShortDto);
        assertThat(result)
                .extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);
        assertThat(result)
                .extractingJsonPathStringValue("$.start")
                .isEqualTo("2023-07-07T12:30:01.35");
        assertThat(result)
                .extractingJsonPathStringValue("$.end")
                .isEqualTo("2023-07-10T15:35:10.15");
    }
}
