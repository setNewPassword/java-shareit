package ru.practicum.shareit.item.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final BookingMapper bookingMapper;
    private final ItemMapper itemMapper;

    @Override
    public Item add(Item item, long userId) {
        User user = userService.getById(userId);
        item = item.toBuilder()
                .owner(user)
                .build();
        item = itemRepository.save(item);
        log.info("Добавлен новый предмет: {}.", item);
        return item;
    }

    @Transactional
    @Override
    public ItemDto update(ItemDto itemDto, long userId, long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(String.format("Предмет с id = %d не найден.", itemId)));

        if ((item.getOwner().getId() != userId)
                && ((itemDto.getName() != null) || (itemDto.getDescription() != null))) {
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
        item = itemRepository.save(item);

        return itemMapper.toDto(item);

        /*User user = userService.getById(userId);
        item = item.toBuilder()
                .id(itemId)
                .owner(user)
                .build();
        checkItemOwner(item);
        Item savedItem = getById(itemId, userId);
        String name = item.getName() == null ? savedItem.getName() : item.getName();
        String description = item.getDescription() == null ? savedItem.getDescription() : item.getDescription();
        boolean available = item.getAvailable() == null ? savedItem.getAvailable() : item.getAvailable();
        item = Item.builder()
                .id(itemId)
                .name(name)
                .description(description)
                .available(available)
                .owner(savedItem.getOwner())
                .build();
        item = itemRepository.save(item);
        log.info("Данные предмета обновлены: {}.", item);
        return item;*/
    }

    @Transactional(readOnly = true)
    @Override
    public ItemDto getById(long itemId, long userId) {
        log.info(String.format("Запрошен предмет с id = %d.", itemId));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(String.format("Предмет с id = %d не найден.", itemId)));
        ItemDto itemDto = itemMapper.toDto(item);

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

        if (itemDto.getLastBooking() == null && itemDto.getNextBooking() != null) {
            itemDto = itemDto
                    .toBuilder()
                    .lastBooking(itemDto.getNextBooking())
                    .nextBooking(null)
                    .build();
        }

        return itemDto;
    }

    @Override
    public void deleteById(long itemId) {
        checkItemExists(itemId);
        itemRepository.deleteById(itemId);
        log.info(String.format("Удален предмет с id = %d.", itemId));
    }

//    @Transactional
    @Override
    public List<ItemDto> getItemsByUserId(long userId) {
        log.info(String.format("Запрошен список предметов, принадлежащих пользователю с id = %d.", userId));
        userService.checkUserExists(userId);
        List<Item> items = itemRepository.findItemsByOwnerId(userId);
        if (items.isEmpty()) {
            return Collections.emptyList();
        }
        System.out.println("!!!!!!!!!!!!!!!!! Items list size = " + items.size());


        List<ItemDto> itemDtoList = items.stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
        System.out.println("!!!!!!!!!!!!!!!!! ItemDTO list size = " + itemDtoList.size());

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
            System.out.println("!!!!!!!!!!!!!!!!! Size of lastBooking = " + lastBooking.size());
            System.out.println("!!!!!!!!!!!!!!!!! Item ID = " + itemDto.getId());
            itemDto = itemDto
                    .toBuilder()
                    .lastBooking(lastBooking.isEmpty() ? null : BookingMapper.toShortDto(lastBooking.get(0)))
                    .build();
            System.out.println("!!!!!!!!!!!!!!!!! Item ID = " + itemDto.getId());
            System.out.println("!!!!!!!!!!!!!!!!! LAST = " + itemDto.getLastBooking());

            List<Booking> nextBooking = bookingRepository.findTop1BookingByItemIdAndStartIsAfterAndStatusIs(
                    itemDto.getId(),
                    LocalDateTime.now(),
                    Status.APPROVED,
                    Sort.by(Sort.Direction.ASC, "start"));
            System.out.println("!!!!!!!!!!!!!!!!! Size of nextBooking = " + nextBooking.size());
            System.out.println("!!!!!!!!!!!!!!!!! Item ID = " + itemDto.getId());
            itemDto = itemDto
                    .toBuilder()
                    .nextBooking(nextBooking.isEmpty() ? null : BookingMapper.toShortDto(nextBooking.get(0)))
                    .build();
            System.out.println("!!!!!!!!!!!!!!!!! Item ID = " + itemDto.getId());
            System.out.println("!!!!!!!!!!!!!!!!! NEXT = " + itemDto.getNextBooking());

            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            System.out.println("Current itemDto = " + itemDto);
        }
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println("!!!!!!!!!!!!!!!!! ItemDTO list = " + itemDtoList);

        /*itemDtoList.sort(Comparator
                .comparing(o -> {
                    if (o.getLastBooking() == null) {
                        return null;
                    } else {
                        return o.getLastBooking().getStart();
                    }
                }, Comparator.nullsLast(Comparator.reverseOrder())));
        System.out.println("!!!!!!!!!!!!!!!!! ItemDTO list = " + itemDtoList);

        for (ItemDto itemDto : itemDtoList) {
            if (itemDto.getLastBooking() != null && itemDto.getLastBooking().getBookerId() == null) {
                itemDto = itemDto
                        .toBuilder()
                        .lastBooking(null)
                        .build();
            }
            System.out.println("!!!!!!!!!!!!!!!!! ItemDTO ID = " + itemDto.getId());
            System.out.println("!!!!!!!!!!!!!!!!! ItemDTO LAST = " + itemDto.getLastBooking());
            System.out.println("!!!!!!!!!!!!!!!!! ItemDTO LastBookingId = " + (itemDto.getLastBooking() != null ? itemDto.getLastBooking().getId() : null));
            System.out.println("!!!!!!!!!!!!!!!!! ItemDTO LastBookingBookerId = " + (itemDto.getLastBooking() != null ? itemDto.getLastBooking().getBookerId() : null));
            
            
            if (itemDto.getNextBooking() != null && itemDto.getNextBooking().getBookerId() == null) {
                itemDto = itemDto
                        .toBuilder()
                        .nextBooking(null)
                        .build();
            }

            System.out.println("!!!!!!!!!!!!!!!!! ItemDTO ID = " + itemDto.getId());
            System.out.println("!!!!!!!!!!!!!!!!! ItemDTO NEXT = " + itemDto.getNextBooking());
            System.out.println("!!!!!!!!!!!!!!!!! ItemDTO NextBookingId = " + (itemDto.getNextBooking() != null ? itemDto.getNextBooking().getId() : null));
            System.out.println("!!!!!!!!!!!!!!!!! ItemDTO NextBookingBookerId = " + (itemDto.getNextBooking() != null ? itemDto.getNextBooking().getBookerId() : null));
        }*/

        return itemDtoList;
    }

    @Override
    public List<Item> getByQuery(String query) {
        if (query == null || query.isBlank()) {
            log.info("Получен пустой запрос для поиска предметов. Был возвращен пустой список.");
            return Collections.emptyList();
        }
        log.info(String.format("Получен запрос для поиска предметов: %s.", query));
        List<Item> result = new ArrayList<>();
        query = query.toLowerCase();

        for (Item item : itemRepository.findAll()) {
            String name = item.getName().toLowerCase();
            String description = item.getDescription().toLowerCase();

            if (item.getAvailable().equals(true) && (name.contains(query) || description.contains(query))) {
                result.add(item);
            }
        }

        return result;
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
        if (!(item.getOwner().getId() == itemRepository.findById(item.getId()).orElseThrow(() ->
                new ItemNotFoundException(String.format("Предмет с id = %d не найден.", item.getId())))
                .getOwner()
                .getId())) {
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
}
