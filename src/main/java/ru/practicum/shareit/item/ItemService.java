package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item add(Item item);

    Item update(Item item);

    Item getById(long itemId);

    void deleteById(long itemId);

    List<Item> getItemsByUserId(long userId);

    List<Item> getByQuery(String query);
}
