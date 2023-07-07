package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemRequestMapperTest {
    private ItemRequest itemRequest;

    private ItemRequestDto itemRequestDto;
    private final ItemRequestMapper itemRequestMapper = Mappers.getMapper(ItemRequestMapper.class);

    @BeforeEach
    public void beforeEach() {
        itemRequest = ItemRequest
                .builder()
                .id(1L)
                .description("looking for a tool to make a hole in the wall with")
                .build();

        itemRequestDto = ItemRequestDto
                .builder()
                .id(1L)
                .description("looking for a tool to make a hole in the wall with")
                .build();
    }

    @Test
    public void toItemRequestDtoTest() {
        ItemRequestDto dto = itemRequestMapper.toDto(itemRequest);

        assertEquals(dto.getId(), itemRequest.getId());
        assertEquals(dto.getDescription(), itemRequest.getDescription());
    }

    @Test
    public void toItemRequestTest() {
        ItemRequest newItemRequest = itemRequestMapper.toEntity(itemRequestDto);

        assertEquals(newItemRequest.getId(), itemRequestDto.getId());
        assertEquals(newItemRequest.getDescription(), itemRequestDto.getDescription());
    }
}