package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@Mapper(componentModel = "spring")
@Service
public interface ItemMapper {
    ItemDto toDto(Item item);

    Item toEntity(ItemDto itemDto);
}