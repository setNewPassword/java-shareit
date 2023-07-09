package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto add(ItemDto itemDto, long userId);

    ItemDto update(ItemDto itemDto, long userId, long itemId);

    ItemDto getById(long itemId, long userId);

    boolean deleteById(long itemId);

    List<ItemDto> getAllItems(long userId, int from, int size);

    List<ItemDto> findAllByQuery(String query, int from, int size);

    CommentDto createComment(Long itemId, Long userId, CommentDto commentDto);
}