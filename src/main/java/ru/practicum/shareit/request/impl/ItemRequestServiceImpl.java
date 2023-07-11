package ru.practicum.shareit.request.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestMapper itemRequestMapper = Mappers.getMapper(ItemRequestMapper.class);

    @Override
    public ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String
                        .format("Пользователь с id = %d не найден.", userId)));
        ItemRequest itemRequest = itemRequestMapper.toEntity(itemRequestDto);
        itemRequest = itemRequest
                .toBuilder()
                .requester(user)
                .build();
        itemRequest = itemRequestRepository.save(itemRequest);
        log.info("Создан новый запрос с id = {}.", itemRequest.getId());

        return itemRequestMapper.toDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> findAllByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(String
                    .format("Пользователь с id = %d не найден.", userId));
        }
        List<ItemRequestDto> itemRequestDtos = itemRequestRepository.findAllByRequesterIdOrderByCreatedAsc(userId)
                .stream()
                .map(itemRequestMapper::toDto)
                .collect(Collectors.toList());
        addItemsToRequests(itemRequestDtos);
        log.info("Возвращен список запросов пользователя с id = {}.", userId);

        return itemRequestDtos;
    }

    @Transactional
    @Override
    public List<ItemRequestDto> getAllByUser(int from, int size, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String
                        .format("Пользователь с id = %d не найден.", userId)));
        List<ItemRequestDto> itemRequestDtos = itemRequestRepository.findAllByRequesterNotLikeOrderByCreatedAsc(user,
                        PageRequest.of(from, size))
                .stream()
                .map(itemRequestMapper::toDto)
                .collect(Collectors.toList());
        addItemsToRequests(itemRequestDtos);
        log.info("Возвращен постраничный список запросов пользователя с id = {}.", userId);

        return itemRequestDtos;
    }

    @Override
    public ItemRequestDto getById(Long requestId, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(String
                    .format("Пользователь с id = %d не найден.", userId));
        }
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new ItemRequestNotFoundException(String
                        .format("Запрос с id = %d не найден.", requestId)));
        ItemRequestDto itemRequestDto = itemRequestMapper.toDto(itemRequest);
        itemRequestDto.addAllItems(itemRepository.findAllByRequestId(itemRequestDto.getId())
                .stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList()));
        log.info("Возвращен запрос с id = {}.", requestId);

        return itemRequestDto;
    }

    private void addItemsToRequests(List<ItemRequestDto> itemRequestDtos) {
        List<Long> requestIds = itemRequestDtos.stream().map(ItemRequestDto::getId).collect(Collectors.toList());
        List<ItemDto> itemDtos = itemRepository.findByRequestIdIn(requestIds)
                .stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());

        if (!itemDtos.isEmpty()) {
            Map<Long, ItemRequestDto> requestsDtosMap = new HashMap<>();
            Map<Long, List<ItemDto>> itemsDtosMap = new HashMap<>();

            itemDtos.forEach(itemDto -> itemsDtosMap.computeIfAbsent(itemDto.getRequestId(), key -> new ArrayList<>()).add(itemDto));
            itemRequestDtos.forEach(request -> requestsDtosMap.put(request.getId(), request));
            itemsDtosMap.forEach((key, value) -> requestsDtosMap.get(key).addAllItems(value));
        }
    }
}