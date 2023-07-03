package ru.practicum.shareit.request;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

@Mapper(componentModel = "spring")
@Service
public interface ItemRequestMapper {
    ItemRequestDto toDto(ItemRequest itemRequest);
    ItemRequest toEntity(ItemRequestDto itemRequestDto);
}
