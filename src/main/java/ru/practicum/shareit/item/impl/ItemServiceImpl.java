package ru.practicum.shareit.item.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;

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

    @Override
    public Item update(Item item, long userId, long itemId) {
        User user = userService.getById(userId);
        item = item.toBuilder()
                .id(itemId)
                .owner(user)
                .build();
        checkItemOwner(item);
        Item savedItem = getById(itemId);
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
        return item;
    }

    @Override
    public Item getById(long itemId) {
        log.info(String.format("Запрошен предмет с id = %d.", itemId));
        return itemRepository.getById(itemId).orElseThrow(() ->
                new ItemNotFoundException(String.format("Предмет с id = %d не найден.", itemId)));
    }

    @Override
    public void deleteById(long itemId) {
        checkItemExists(itemId);
        itemRepository.deleteById(itemId);
        log.info(String.format("Удален предмет с id = %d.", itemId));
    }

    @Override
    public List<Item> getItemsByUserId(long userId) {
        userService.checkUserExists(userId);
        return itemRepository.findItemsByUserId(userId);
    }

    @Override
    public List<Item> getByQuery(String query) {
        if (query == null || query.isBlank()) {
            log.info("Получен пустой запрос для поиска предметов. Был возвращен пустой список.");
            return Collections.emptyList();
        }
        log.info(String.format("Получен запрос для поиска предметов: %s.", query));
        return itemRepository.findByQuery(query);
    }

    private void checkItemOwner(Item item) {
        if (!itemRepository.isSameOwner(item)) {
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
