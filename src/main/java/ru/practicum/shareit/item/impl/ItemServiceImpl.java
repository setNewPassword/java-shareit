package ru.practicum.shareit.item.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final ItemRequestRepository itemRequestRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

    @Override
    public ItemDto add(ItemDto itemDto, long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String
                        .format("Пользователь с id = %d не найден.", userId)));
        Item item = ItemMapper.toEntity(itemDto);
        if (itemDto.getRequestId() != null) {
            ItemRequest itemRequest = itemRequestRepository
                    .findById(itemDto.getRequestId())
                    .orElseThrow(() -> new ItemRequestNotFoundException(String
                            .format("Запрос с id = %d не найден.", itemDto.getRequestId())));
            item.setRequest(itemRequest);
        }
        item = item.toBuilder()
                .owner(user)
                .build();
        item = itemRepository.save(item);
        log.info("Добавлен новый предмет: {}.", item);

        return ItemMapper.toDto(item);
    }

    @Transactional
    @Override
    public ItemDto update(ItemDto itemDto, long userId, long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(String.format("Предмет с id = %d не найден.", itemId)));

        if (item.getOwner().getId() != userId) {
            throw new UserNotFoundException(String
                    .format("Доступ запрещен! Пользователь с id = %d не является владельцем предмета с id = %d.",
                            userId,
                            itemId));
        }

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        if (itemDto.getRequestId() != null) {
            ItemRequest itemRequest = itemRequestRepository
                    .findById(itemDto.getRequestId())
                    .orElseThrow(() -> new ItemRequestNotFoundException(String
                            .format("Запрос с id = %d не найден.", itemDto.getRequestId())));
            item.setRequest(itemRequest);
        }

        item = itemRepository.save(item);

        return ItemMapper.toDto(item);
    }

    @Transactional(readOnly = true)
    @Override
    public ItemDto getById(long itemId, long userId) {
        log.info(String.format("Запрошен предмет с id = %d.", itemId));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(String.format("Предмет с id = %d не найден.", itemId)));
        ItemDto itemDto = ItemMapper.toDto(item);

        itemDto = itemDto
                .toBuilder()
                .comments(commentRepository.findAllByItemId(itemId)
                        .stream()
                        .map(CommentMapper::toDto)
                        .collect(Collectors.toList()))
                .build();

        if (!(item.getOwner().getId() == userId)) {
            return itemDto;
        }

        List<Booking> lastBooking = bookingRepository.findTop1BookingByItemIdAndStartIsBeforeAndStatusIs(
                itemId,
                LocalDateTime.now(),
                Status.APPROVED,
                Sort.by(Sort.Direction.DESC, "start"));
        itemDto = itemDto
                .toBuilder()
                .lastBooking(lastBooking.isEmpty() ? null : BookingMapper.toShortDto(lastBooking.get(0)))
                .build();

        List<Booking> nextBooking = bookingRepository.findTop1BookingByItemIdAndStartIsAfterAndStatusIs(
                itemId,
                LocalDateTime.now(),
                Status.APPROVED,
                Sort.by(Sort.Direction.ASC, "start"));
        itemDto = itemDto
                .toBuilder()
                .nextBooking(nextBooking.isEmpty() ? null : BookingMapper.toShortDto(nextBooking.get(0)))
                .build();

        return itemDto;
    }

    @Override
    public void deleteById(long itemId) {
        checkItemExists(itemId);
        itemRepository.deleteById(itemId);
        log.info(String.format("Удален предмет с id = %d.", itemId));
    }

    @Override
    public List<ItemDto> getAllItems(long userId, int from, int size) {
        checkParameters(from, size);
        log.info(String.format("Запрошен список предметов, принадлежащих пользователю с id = %d.", userId));
        userService.checkUserExists(userId);
        List<Item> items = itemRepository.findAllByOwnerId(userId, PageRequest.of(from, size));
        if (items.isEmpty()) {
            return Collections.emptyList();
        }


        List<ItemDto> itemDtoList = items.stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());

        List<ItemDto> fullItemDtoList = new ArrayList<>();

        for (ItemDto itemDto : itemDtoList) {
            itemDto = itemDto
                    .toBuilder()
                    .comments(commentRepository.findAllByItemId(itemDto.getId())
                            .stream()
                            .map(CommentMapper::toDto)
                            .collect(Collectors.toList()))
                    .build();

            List<Booking> lastBooking = bookingRepository.findTop1BookingByItemIdAndStartIsBeforeAndStatusIs(
                    itemDto.getId(),
                    LocalDateTime.now(),
                    Status.APPROVED,
                    Sort.by(Sort.Direction.DESC, "start"));

            itemDto = itemDto
                    .toBuilder()
                    .lastBooking(lastBooking.isEmpty() ? null : BookingMapper.toShortDto(lastBooking.get(0)))
                    .build();

            List<Booking> nextBooking = bookingRepository.findTop1BookingByItemIdAndStartIsAfterAndStatusIs(
                    itemDto.getId(),
                    LocalDateTime.now(),
                    Status.APPROVED,
                    Sort.by(Sort.Direction.ASC, "start"));

            itemDto = itemDto
                    .toBuilder()
                    .nextBooking(nextBooking.isEmpty() ? null : BookingMapper.toShortDto(nextBooking.get(0)))
                    .build();

            fullItemDtoList.add(itemDto);
        }

        fullItemDtoList.sort(Comparator
                .comparing(o -> {
                    if (o.getLastBooking() == null) {
                        return null;
                    } else {
                        return o.getLastBooking().getStart();
                    }
                }, Comparator.nullsLast(Comparator.reverseOrder())));

        for (ItemDto itemDto : fullItemDtoList) {
            if (itemDto.getLastBooking() != null && itemDto.getLastBooking().getBookerId() == null) {
                itemDto = itemDto
                        .toBuilder()
                        .lastBooking(null)
                        .build();
            }

            if (itemDto.getNextBooking() != null && itemDto.getNextBooking().getBookerId() == null) {
                itemDto = itemDto
                        .toBuilder()
                        .nextBooking(null)
                        .build();
            }
        }
        return fullItemDtoList;
    }

    @Override
    public List<ItemDto> findAllByQuery(String query, int from, int size) {
        checkParameters(from, size);
        if (query == null || query.isBlank()) {
            log.info("Получен пустой запрос для поиска предметов. Был возвращен пустой список.");
            return Collections.emptyList();
        }
        log.info(String.format("Получен запрос для поиска предметов: %s.", query));
        return itemRepository
                .findAllByQuery(query.toLowerCase(), PageRequest.of(from, size))
                .stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public CommentDto createComment(Long itemId, Long userId, CommentDto commentDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String
                        .format("Пользователь с id = %d не найден.", userId)));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(String
                        .format("Предмет с id = %d не найден.", itemId)));
        if (bookingRepository
                .findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(userId,
                        itemId,
                        Status.APPROVED,
                        LocalDateTime.now())
                .isEmpty()) {
            throw new IllegalArgumentException("Пользователь не имеет права комментировать этот предмет.");
        }
        Comment comment = commentMapper.toEntity(commentDto);
        comment = comment
                .toBuilder()
                .item(item)
                .author(user)
                .created(LocalDateTime.now())
                .build();
        comment = commentRepository.save(comment);
        log.info(String.format("Пользователь с id = %d оставил комментарий с id = %d к предмету с id = %d.",
                userId,
                comment.getId(),
                itemId));

        return CommentMapper.toDto(comment);
    }

    private void checkItemOwner(Item item) {
        if (!(Objects.equals(item.getOwner().getId(), itemRepository.findById(item.getId()).orElseThrow(() ->
                        new ItemNotFoundException(String.format("Предмет с id = %d не найден.", item.getId())))
                .getOwner()
                .getId()))) {
            throw new ItemNotFoundException(
                    String.format("Пользователь с id = %d не является владельцем предмета с id = %d.",
                            item.getOwner().getId(),
                            item.getId()));
        }
    }

    private void checkItemExists(long itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new ItemNotFoundException(String.format("Предмет с id = %d не найден.", itemId));
        }
    }

    private void checkParameters(int from, int size) {
        if (from < 0 || size < 1) {
            throw new IllegalArgumentException("Неверные параметры запроса from и (или) size.");
        }
    }
}
