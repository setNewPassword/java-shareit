package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemMapperTest {
    private Item item;
    private ItemDto itemDto;

    @BeforeEach
    public void prepareModels() {
        item = Item
                .builder()
                .id(1L)
                .name("Screwdriver")
                .description("tool, usually hand-operated, for turning screws with slotted heads")
                .available(true)
                .build();
        itemDto = ItemDto
                .builder()
                .id(1L)
                .name("DTO screwdriver")
                .available(true)
                .build();

    }

    @Test
    public void toDtoTest() {
        ItemDto dto = ItemMapper.toDto(item);

        assertEquals(dto.getId(), item.getId());
        assertEquals(dto.getName(), item.getName());
        assertEquals(dto.getDescription(), item.getDescription());
    }

    @Test
    public void toEntityTest() {
        Item newItem = ItemMapper.toEntity(itemDto);

        assertEquals(newItem.getId(), itemDto.getId());
        assertEquals(newItem.getName(), itemDto.getName());
        assertEquals(newItem.getDescription(), itemDto.getDescription());
    }
}