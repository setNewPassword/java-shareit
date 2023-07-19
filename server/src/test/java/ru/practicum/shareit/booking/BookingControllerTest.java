package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.item.model.Item;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
public class BookingControllerTest {

    @MockBean
    private BookingService bookingService;

    private final ObjectMapper mapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();

    @Autowired
    private MockMvc mvc;

    private BookingDto bookingDto;

    @BeforeEach
    public void beforeEach() {
        bookingDto = BookingDto
                .builder()
                .id(1L)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusDays(1))
                .build();
    }

    @Test
    @SneakyThrows
    public void createBookingTest() {
        BookingShortDto inputBookingDto = BookingShortDto
                .builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusDays(1))
                .build();

        when(bookingService.create(any(BookingShortDto.class), any(Long.class)))
                .thenReturn(bookingDto);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(inputBookingDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.item", is(bookingDto.getItem()), Item.class));

        verify(bookingService, times(1))
                .create(any(BookingShortDto.class), any(Long.class));
    }

    @Test
    @SneakyThrows
    public void approveBookingTest() {
        when(bookingService.approve(any(Long.class), any(Long.class), any(Boolean.class)))
                .thenReturn(bookingDto);

        mvc.perform(patch("/bookings/1")
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class));

        verify(bookingService, times(1))
                .approve(any(Long.class), any(Long.class), any(Boolean.class));
    }

    @Test
    @SneakyThrows
    public void getByIdTest() {
        when(bookingService.getById(any(Long.class), any(Long.class)))
                .thenReturn(bookingDto);

        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class));

        verify(bookingService, times(1))
                .getById(any(Long.class), any(Long.class));
    }

    @Test
    @SneakyThrows
    public void getAllByUserTest() {
        when(bookingService.getAllByUser(any(Long.class), any(State.class), any(Integer.class),
                any(Integer.class)))
                .thenReturn(new ArrayList<>());

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "10")
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(bookingService, times(1))
                .getAllByUser(any(Long.class), any(State.class), any(Integer.class), any(Integer.class));
    }

    @Test
    @SneakyThrows
    public void getAllByOwnerTest() {
        when(bookingService.getAllByOwner(any(Long.class), any(State.class), any(Integer.class),
                any(Integer.class)))
                .thenReturn(new ArrayList<>());

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(bookingService, times(1))
                .getAllByOwner(any(Long.class), any(State.class), any(Integer.class), any(Integer.class));
    }


    @Test
    @SneakyThrows
    public void getAllByOwnerUnknownStateTest() {
        when(bookingService.getAllByOwner(any(Long.class), any(State.class), any(Integer.class),
                any(Integer.class)))
                .thenThrow(new BadRequestException(State.UNSUPPORTED_STATUS.name()));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "10")
                        .param("state", "foo"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Unknown state: UNSUPPORTED_STATUS")));

        verify(bookingService, times(1))
                .getAllByOwner(any(Long.class), any(State.class), any(Integer.class), any(Integer.class));
    }
}
