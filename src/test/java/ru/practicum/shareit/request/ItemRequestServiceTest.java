package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.ItemRepository;
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
    void getAllByUserTest() {
        when(userRepository.existsById(any()))
                .thenReturn(true);

        when(itemRequestRepository
                .findAllByRequesterIdOrderByCreatedAsc(any(Long.class)))
                .thenReturn(new ArrayList<>());

        List<ItemRequestDto> result = itemRequestService.findAllByUser(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllTest() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(homerSimpson));

        when(itemRequestRepository.findAllByRequesterNotLikeOrderByCreatedAsc(any(User.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(new ArrayList<>()));

        List<ItemRequestDto> result = itemRequestService.getAllByUser(0, 10, 1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
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