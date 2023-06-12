package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item add(Item item, long userId);

    Item update(Item item, long userId, long itemId);

    Item getById(long itemId);

    void deleteById(long itemId);

    List<Item> getItemsByUserId(long userId);

    List<Item> getByQuery(String query);
}
