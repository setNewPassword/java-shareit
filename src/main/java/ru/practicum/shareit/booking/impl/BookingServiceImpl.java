package ru.practicum.shareit.booking.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;
    private final Sort sort = Sort.by(Sort.Direction.DESC, "start");

    @Override
    public BookingDto create(BookingShortDto bookingShortDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String
                        .format("Пользователь с id = %d не найден.", userId)));
        Item item = itemRepository.findById(bookingShortDto.getItemId())
                .orElseThrow(() -> new ItemNotFoundException(String
                        .format("Предмет с id = %d не найден.", bookingShortDto.getItemId())));

        if (Objects.equals(item.getOwner().getId(), userId)) {
            throw new BookingNotFoundException(String
                    .format("Пользователь с id = %d не может забронировать свой же предмет.", userId));
        }
        if (!item.getAvailable()) {
            throw new IllegalArgumentException(String
                    .format("Предмет с id = %d не доступен для бронирования.", item.getId()));
        }
        Booking booking = bookingMapper.toEntity(bookingShortDto);
        if (booking.getEnd().isBefore(booking.getStart()) || booking.getEnd().isEqual(booking.getStart())) {
            throw new IllegalArgumentException("Неверные параметры даты начала и окончания аренды.");
        }

        booking = booking
                .toBuilder()
                .booker(user)
                .item(item)
                .status(Status.WAITING)
                .build();

        long bookingId = bookingRepository.save(booking).getId();
        log.info(String.format("Добавлено бронирование с id = %d", bookingId));

        return bookingMapper.toDto(booking);
    }

    @Transactional
    @Override
    public BookingDto approve(Long bookingId, Long userId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(String
                        .format("Бронирование с id = %d не найдено.", bookingId)));
        if (!userId.equals(booking.getItem().getOwner().getId())) {
            throw new UserNotFoundException(String
                    .format("Пользователь с id = %d не является владельцем предмета с id = %d.",
                            userId,
                            booking.getItem().getId()));
        }
        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new IllegalArgumentException("Бронирование не нуждается в подтверждении.");
        }
        if (approved) {
            booking.setStatus(Status.APPROVED);
            log.info(String.format("Бронирование с id = %d подтверждено.", booking.getId()));
        } else {
            booking.setStatus(Status.REJECTED);
            log.info(String.format("Бронирование с id = %d отклонено.", booking.getId()));
        }
        bookingRepository.save(booking);

        return bookingMapper.toDto(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> getAllByOwner(Long ownerId, State state, int from, int size) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new UserNotFoundException(String
                        .format("Пользователь с id = %d не найден.", ownerId)));
        List<Booking> bookingsList = new ArrayList<>();
        PageRequest pageRequest = PageRequest.of(from / size, size, sort);

        switch (state) {
            case ALL:
                bookingsList.addAll(bookingRepository
                        .findAllByItemOwner(owner, pageRequest)
                        .toList());
                log.info(String.format("Запрошены все бронирования пользователя-владельца с id = %d.", ownerId));
                break;

            case FUTURE:
                bookingsList.addAll(bookingRepository
                        .findAllByItemOwnerAndStartAfter(owner, LocalDateTime.now(), pageRequest)
                        .toList());
                log.info(String.format("Запрошены будущие бронирования пользователя-владельца с id = %d.", ownerId));
                break;

            case CURRENT:
                bookingsList.addAll(bookingRepository
                        .findAllByItemOwnerAndStartBeforeAndEndAfter(owner,
                                LocalDateTime.now(),
                                LocalDateTime.now(),
                                pageRequest)
                        .toList());
                log.info(String.format("Запрошены текущие бронирования пользователя-владельца с id = %d.", ownerId));
                break;

            case PAST:
                bookingsList.addAll(bookingRepository
                        .findAllByItemOwnerAndEndBefore(owner,
                                LocalDateTime.now(),
                                pageRequest)
                        .toList());
                log.info(String
                        .format("Запрошены завершенные бронирования пользователя-владельца с id = %d.", ownerId));
                break;

            case WAITING:
                bookingsList.addAll(bookingRepository
                        .findAllByItemOwnerAndStatusEquals(owner,
                                Status.WAITING,
                                pageRequest)
                        .toList());
                log.info(String
                        .format("Запрошены все бронирования пользователя-владельца с id = %d, ожидающие подтверждения.",
                                ownerId));
                break;

            case REJECTED:
                bookingsList.addAll(bookingRepository
                        .findAllByItemOwnerAndStatusEquals(owner,
                                Status.REJECTED,
                                pageRequest)
                        .toList());
                log.info(String
                        .format("Запрошены отклоненные бронирования пользователя-владельца с id = %d.", ownerId));
                break;

            default:
                log.warn(String.format("Внимание! Получен запрос с неизвестным статусом — %s.", state));
                throw new BadRequestException(state.name());
        }

        return bookingsList
                .stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> getAllByUser(Long bookerId, State state, int from, int size) {
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new UserNotFoundException(String
                        .format("Пользователь с id = %d не найден.", bookerId)));
        List<Booking> bookingDtoList = new ArrayList<>();
        PageRequest pageRequest = PageRequest.of(from / size, size, sort);

        switch (state) {
            case ALL:
                bookingDtoList.addAll(bookingRepository
                        .findAllByBooker(booker, pageRequest)
                        .toList());
                log.info(String.format("Запрошены все бронирования пользователя-букера с id = %d.", bookerId));
                break;

            case FUTURE:
                bookingDtoList.addAll(bookingRepository
                        .findAllByBookerAndStartAfter(booker,
                                LocalDateTime.now(),
                                pageRequest)
                        .toList());
                log.info(String.format("Запрошены будущие бронирования пользователя-букера с id = %d.", bookerId));
                break;

            case CURRENT:
                bookingDtoList.addAll(bookingRepository
                        .findAllByBookerAndStartBeforeAndEndAfter(booker,
                                LocalDateTime.now(),
                                LocalDateTime.now(),
                                pageRequest)
                        .toList());
                log.info(String.format("Запрошены текущие бронирования пользователя-букера с id = %d.", bookerId));
                break;

            case PAST:
                bookingDtoList.addAll(bookingRepository
                        .findAllByBookerAndEndBefore(booker,
                                LocalDateTime.now(),
                                pageRequest)
                        .toList());
                log.info(String
                        .format("Запрошены завершенные бронирования пользователя-букера с id = %d.", bookerId));
                break;

            case WAITING:
                bookingDtoList.addAll(bookingRepository
                        .findAllByBookerAndStatusEquals(booker,
                                Status.WAITING,
                                pageRequest)
                        .toList());
                log.info(String
                        .format("Запрошены все бронирования пользователя-букера с id = %d, ожидающие подтверждения.",
                                bookerId));
                break;

            case REJECTED:
                bookingDtoList.addAll(bookingRepository
                        .findAllByBookerAndStatusEquals(booker,
                                Status.REJECTED,
                                pageRequest)
                        .toList());
                log.info(String
                        .format("Запрошены отклоненные бронирования пользователя-букера с id = %d.", bookerId));
                break;

            default:
                log.warn(String.format("Внимание! Получен запрос с неизвестным статусом — %s.", state));
                throw new BadRequestException(state.name());
        }

        return bookingDtoList
                .stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public BookingDto getById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(String
                        .format("Бронирование с id = %d не найдено.", bookingId)));
        if (!userId.equals(booking.getBooker().getId()) && !userId.equals(booking.getItem().getOwner().getId())) {
            throw new BookingNotFoundException(
                    "Доступ запрещен — вы не являетесь владельцем предмета и не являетесь его арендатором.");
        }

        return bookingMapper.toDto(booking);
    }
}