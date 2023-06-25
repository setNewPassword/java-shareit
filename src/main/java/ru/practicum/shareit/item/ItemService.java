package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item add(Item item, long userId);

    ItemDto update(ItemDto itemDto, long userId, long itemId);

    ItemDto getById(long itemId, long userId);

    void deleteById(long itemId);

    List<ItemDto> getItemsByUserId(long userId);

    List<Item> getByQuery(String query);

    CommentDto createComment(Long itemId, Long userId, CommentDto commentDto);
}