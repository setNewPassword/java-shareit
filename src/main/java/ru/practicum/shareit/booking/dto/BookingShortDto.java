package ru.practicum.shareit.booking.dto;

import lombok.*;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Builder(toBuilder = true)
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BookingShortDto {

    Long id;

    @NotNull
    @FutureOrPresent
    LocalDateTime start;

    @NotNull
    @FutureOrPresent
    LocalDateTime end;

    Long itemId;

    Long bookerId;
}
