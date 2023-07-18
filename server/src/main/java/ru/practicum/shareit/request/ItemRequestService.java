package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> findAllByUser(Long userId);

    List<ItemRequestDto> getAllByUser(int from, int size, Long userId);

    ItemRequestDto getById(Long requestId, Long userId);
}