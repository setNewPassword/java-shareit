package ru.practicum.shareit.request.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemMapper itemMapper;
    @Override
    public ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String
                        .format("Пользователь с id = %d не найден.", userId)));
        ItemRequest itemRequest = itemRequestMapper.toEntity(itemRequestDto);
        itemRequest = itemRequest
                .toBuilder()
                .created(LocalDateTime.now())
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
        itemRequestDtos.forEach(this::setItemsToItemRequestDto);
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
        itemRequestDtos.forEach(this::setItemsToItemRequestDto);
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
        setItemsToItemRequestDto(itemRequestDto);
        log.info("Возвращен запрос с id = {}.", requestId);

        return itemRequestDto;
    }

    private void setItemsToItemRequestDto(ItemRequestDto itemRequestDto) {
        itemRequestDto.setItems(itemRepository.findAllByRequestId(itemRequestDto.getId())
                        .stream()
                        .map(itemMapper::toDto)
                        .collect(Collectors.toList()));
    }
}