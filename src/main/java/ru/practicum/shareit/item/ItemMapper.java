package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

public interface ItemMapper {
    ItemDto toDto(Item item);

    Item toEntity(ItemDto itemDto);
}
