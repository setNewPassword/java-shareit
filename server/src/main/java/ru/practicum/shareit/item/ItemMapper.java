package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@Mapper(componentModel = "spring")
@Service
public interface ItemMapper {
    @Named("itemToItemDto")
    static ItemDto toDto(Item item) {
        return ItemDto
                .builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .build();
    }

    @Named("itemDtoToItem")
    static Item toEntity(ItemDto itemDto) {
        return Item
                .builder()
                .id(itemDto.getId() != 0 ? itemDto.getId() : null)
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }

}