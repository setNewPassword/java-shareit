package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.impl.BookingServiceImpl;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private BookingServiceImpl bookingService;
    @Mock
    private BookingRepository bookingRepository;
    private User user;
    private Item item;
    private User owner;
    private Booking booking;
    private BookingDto bookingDto;
    private BookingShortDto bookingShortDto;
    private final BookingMapper bookingMapper = Mappers.getMapper(BookingMapper.class);

    @BeforeEach
    public void beforeEach() {
        user = new User(1L, "Дмитрий", "dee.irk@gmail.com");
        owner = new User(2L, "Аркадий", "volozh@yandex.ru");
        item = new Item(1L,
                "Перфоратор",
                "Отличный инструмент, чтоб будить соседей ранним субботним утром",
                true,
                owner,
                null);
        booking = new Booking(1L,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                item,
                user,
                Status.APPROVED);
        bookingDto = bookingMapper.toDto(booking);
        bookingShortDto = BookingMapper.toShortDto(booking);
    }

    @Test
    public void createBookingTest() {

        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(owner));

        when(itemRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        BookingDto result = bookingService.create(bookingShortDto, 1L);

        assertEquals(bookingDto.getItem().getId(), result.getItem().getId());
        assertEquals(bookingDto.getStart(), result.getStart());
        assertEquals(bookingDto.getEnd(), result.getEnd());
    }

    @Test
    public void approveBookingTest() {
        booking.setStatus(Status.WAITING);

        when(bookingRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(booking));

        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        BookingDto result = bookingService.approve(1L, 2L, true);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(Status.APPROVED, result.getStatus());
    }


    @Test
    public void getByIdTest() {
        item.setOwner(owner);

        when(bookingRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(booking));

        BookingDto result = bookingService.getById(1L, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    public void findAllByBookerStateRejectedTest() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(user));

        when(bookingRepository
                .findAllByBookerAndStatusEquals(any(User.class), any(Status.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(booking)));

        List<BookingDto> result = bookingService.getAllByUser(1L, State.REJECTED, 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void findAllByBookerStateWaitingTest() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(user));

        when(bookingRepository
                .findAllByBookerAndStatusEquals(any(User.class), any(Status.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(booking)));

        List<BookingDto> result = bookingService.getAllByUser(1L, State.WAITING, 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void findAllByBookerStateCurrentTest() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(user));

        when(bookingRepository
                .findAllByBookerAndStartBeforeAndEndAfter(any(User.class), any(LocalDateTime.class),
                        any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(booking)));

        List<BookingDto> result = bookingService.getAllByUser(1L, State.CURRENT, 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void findAllByBookerStateFutureTest() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(user));

        when(bookingRepository
                .findAllByBookerAndStartAfter(any(User.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(booking)));

        List<BookingDto> result = bookingService.getAllByUser(1L, State.FUTURE, 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void findAllByBookerStatePastTest() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(user));

        when(bookingRepository
                .findAllByBookerAndEndBefore(any(User.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(booking)));

        List<BookingDto> result = bookingService.getAllByUser(1L, State.PAST, 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void findAllByBookerStateAllTest() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(user));

        when(bookingRepository
                .findAllByBooker(any(User.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDto> result = bookingService
                .getAllByUser(1L, State.ALL, 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }


    @Test
    public void findAllByItemOwnerStateWaitingTest() {

        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(user));

        when(bookingRepository
                .findAllByItemOwnerAndStatusEquals(any(User.class), any(Status.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(booking)));

        List<BookingDto> result = bookingService.getAllByOwner(1L, State.WAITING, 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void findAllByItemOwnerStateCurrentTest() {

        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(user));

        when(bookingRepository
                .findAllByItemOwnerAndStartBeforeAndEndAfter(any(User.class), any(LocalDateTime.class),
                        any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(booking)));

        List<BookingDto> result = bookingService.getAllByOwner(1L, State.CURRENT, 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void findAllByItemOwnerStateFutureTest() {

        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(user));

        when(bookingRepository
                .findAllByItemOwnerAndStartAfter(any(User.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(booking)));

        List<BookingDto> result = bookingService.getAllByOwner(1L, State.FUTURE, 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void findAllByItemOwnerStatePastTest() {

        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(user));

        when(bookingRepository
                .findAllByItemOwnerAndEndBefore(any(User.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(booking)));

        List<BookingDto> result = bookingService.getAllByOwner(1L, State.PAST, 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void findAllByItemOwnerStateAllTest() {

        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(user));

        when(bookingRepository
                .findAllByItemOwner(any(User.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(booking)));

        List<BookingDto> result = bookingService.getAllByOwner(1L, State.ALL, 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void findAllByItemOwnerStateRejectedTest() {

        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(user));

        when(bookingRepository
                .findAllByItemOwnerAndStatusEquals(any(User.class), any(Status.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(booking)));

        List<BookingDto> result = bookingService
                .getAllByOwner(1L, State.REJECTED, 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void createBookingWrongDateTest() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(owner));

        when(itemRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(item));

        bookingShortDto.setStart(LocalDateTime.now());
        bookingShortDto.setEnd(LocalDateTime.now().minusDays(1));

        Exception e = assertThrows(IllegalArgumentException.class,
                () -> bookingService.create(bookingShortDto, 1L));
        assertNotNull(e);
    }

    @Test
    public void approveBookingWrongUserTest() {
        booking.setStatus(Status.WAITING);

        when(bookingRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(booking));

        Exception e = assertThrows(UserNotFoundException.class,
                () -> bookingService.approve(1L, 1L, true));
        assertNotNull(e);
    }

    @Test
    public void approveBookingWrongBookingIdTest() {
        booking.setStatus(Status.WAITING);

        when(bookingRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(booking));

        Exception e = assertThrows(UserNotFoundException.class,
                () -> bookingService.approve(7L, 1L, true));
        assertNotNull(e);
    }

    @Test
    public void createBookingWrongOwnerTest() {

        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(null));

        Exception e = assertThrows(UserNotFoundException.class,
                () -> bookingService.create(bookingShortDto, 1L));
        assertNotNull(e);
    }

    @Test
    public void createBookingWrongItemTest() {

        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(owner));

        when(itemRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(null));

        Exception e = assertThrows(ItemNotFoundException.class,
                () -> bookingService.create(bookingShortDto, 1L));
        assertNotNull(e);
    }

    @Test
    public void approveBookingWrongUserIdTest() {
        booking.setStatus(Status.WAITING);

        when(bookingRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(booking));

        Exception e = assertThrows(UserNotFoundException.class,
                () -> bookingService.approve(1L, 1L, true));
        assertNotNull(e);
    }
}
