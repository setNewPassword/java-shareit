package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.impl.ItemRequestServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;
    private ItemRequest itemRequest;
    private User homerSimpson;
    private Item item;

    @BeforeEach
    public void beforeEach() {

        homerSimpson = User
                .builder()
                .id(1L)
                .name("Homer Simpson")
                .email("homer@springfield.net")
                .build();
        itemRequest = ItemRequest
                .builder()
                .id(1L)
                .description("looking for a tool to make a hole in the wall with")
                .requester(homerSimpson)
                .created(LocalDateTime.parse("2023-07-07T15:46:59.59"))
                .build();
        item = Item
                .builder()
                .id(1L)
                .name("Screwdriver")
                .description("Phillips head screwdriver")
                .owner(homerSimpson)
                .available(true)
                .request(itemRequest)
                .build();
    }

    @Test
    public void createTest() {
        ItemRequestDto inputDto = ItemRequestDto
                .builder()
                .id(1L)
                .description(itemRequest.getDescription())
                .build();

        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(homerSimpson));

        when(itemRequestRepository.save(any(ItemRequest.class)))
                .thenReturn(itemRequest);

        ItemRequestDto responseDto = itemRequestService.create(1L, inputDto);

        assertEquals(1L, responseDto.getId());
        assertEquals(inputDto.getDescription(), responseDto.getDescription());
    }

    @Test
    void findAllByUserTest() {
        when(userRepository.existsById(any()))
                .thenReturn(true);

        when(itemRequestRepository.findAllByRequesterId(any(Long.class), any(Sort.class)))
                .thenReturn(List.of(itemRequest));
        when(itemRepository.findByRequestIdIn(any()))
                .thenReturn(List.of(item));

        List<ItemRequestDto> result = itemRequestService.findAllByUser(1L);

        assertNotNull(result);
        assertEquals(result.get(0).getItems().get(0), ItemMapper.toDto(item));
    }

    @Test
    void getAllByUserTest() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(homerSimpson));

        when(itemRequestRepository.findAllByRequesterNot(any(User.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(new ArrayList<>()));

        List<ItemRequestDto> result = itemRequestService.getAllByUser(0, 10, 1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllByUserNotExistsTest() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);

        UserNotFoundException e = assertThrows(UserNotFoundException.class,
                () -> itemRequestService.getById(1L, 1L));

        assertNotNull(e);
        assertEquals("Пользователь с id = 1 не найден.", e.getMessage());
    }

    @Test
    void getAllByUserRequestNotFoundTest() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        ItemRequestNotFoundException e = assertThrows(ItemRequestNotFoundException.class,
                () -> itemRequestService.getById(1L, 1L));

        assertNotNull(e);
        assertEquals("Запрос с id = 1 не найден.", e.getMessage());
    }

    @Test
    void getByIdTest() {
        when(userRepository.existsById(any(Long.class)))
                .thenReturn(true);

        when(itemRequestRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(itemRequest));

        when(itemRepository.findAllByRequestId(any(Long.class)))
                .thenReturn(new ArrayList<>());


        ItemRequestDto result = itemRequestService.getById(1L, 1L);

        assertEquals(1L, result.getId());
        assertEquals(itemRequest.getDescription(), result.getDescription());
        assertNotNull(result.getItems());
        assertTrue(result.getItems().isEmpty());
    }
}