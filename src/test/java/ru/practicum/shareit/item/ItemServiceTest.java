package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.impl.ItemServiceImpl;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserService userService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @InjectMocks
    private ItemServiceImpl itemService;
    private Item item;
    private User user;
    private ItemDto itemDto;
    private Comment comment;
    private Booking booking;
    private CommentDto commentDto;
    private ItemRequest itemRequest;

    @BeforeEach
    public void beforeEach() {
        user = new User(1L, "Homer Simpson", "homer@springfield.net");
        itemRequest = new ItemRequest(1L, "Screwdriver", new User(2L, "Stanley Randall Marsh",
                "stan@southpark.net"), LocalDateTime.now());
        item = new Item(1L, "Phillips head screwdriver",
                "It has a head with pointed edges in the shape of a cross", true, user, itemRequest);
        itemDto = ItemMapper.toDto(item);
        comment = new Comment(1L, "It's awesome!", item, user, LocalDateTime.now());
        commentDto = CommentMapper.toDto(comment);
        booking = new Booking(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), item, user,
                Status.APPROVED);
    }

    @Test
    public void createItemTest() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(user));

        when(itemRepository.save(any(Item.class)))
                .thenReturn(item);

        when(itemRequestRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(itemRequest));

        ItemDto result = itemService.add(itemDto, 1L);

        assertEquals(itemDto.getName(), result.getName());
        assertEquals(itemDto.getDescription(), result.getDescription());
        assertEquals(itemDto.getAvailable(), result.getAvailable());
        assertEquals(itemDto.getRequestId(), result.getRequestId());
    }

    @Test
    public void createItemNoUserTest() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.empty());

        UserNotFoundException result = assertThrows(UserNotFoundException.class,
                () -> itemService.createComment(1L, 1L, commentDto));

        assertNotNull(result);
    }

    @Test
    public void createCommentTest() {
        when(itemRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(item));

        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(user));

        when(bookingRepository
                .findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(any(Long.class), any(Long.class),
                        any(Status.class), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        when(commentRepository.save(any(Comment.class)))
                .thenReturn(comment);

        CommentDto result = itemService.createComment(1L, 1L, commentDto);

        assertEquals(commentDto.getText(), result.getText());
        assertEquals(user.getName(), result.getAuthorName());
    }

    @Test
    public void updateItemTest() {
        itemDto = itemDto.toBuilder()
                .name("Hammer")
                .build();
        item = item
                .toBuilder()
                .name("Hammer")
                .build();

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(itemRepository.save(any()))
                .thenReturn(item);
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(itemRequest));

        ItemDto result = itemService.update(itemDto, itemDto.getId(), user.getId());

        assertNotNull(result);
        assertEquals(itemDto.getId(), result.getId());
        assertEquals(itemDto.getName(), result.getName());
    }

    @Test
    public void updateItemWrongOwnerTest() {
        itemDto = itemDto.toBuilder()
                .name("Wood planer")
                .build();
        item = item
                .toBuilder()
                .name("Wood planer")
                .build();

        when(itemRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(item));

        Exception e = assertThrows(ItemRequestNotFoundException.class,
                () -> itemService.update(itemDto, itemDto.getId(), 7L));
        assertNotNull(e);
    }

    @Test
    public void findItemByIdTest() {
        booking.setStart(LocalDateTime.now());
        booking.setEnd(null);

        when(itemRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(item));

        when(commentRepository.findAllByItemId(any(Long.class)))
                .thenReturn(new ArrayList<>());

        ItemDto result = itemService.getById(1L, 1L);

        assertEquals(1L, result.getId());
        assertTrue(result.getComments().isEmpty());
    }

    @Test
    public void findItemByIdNoItemTest() {
        when(itemRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(null));

        ItemNotFoundException result = assertThrows(ItemNotFoundException.class,
                () -> itemService.getById(1L, 1L));

        assertNotNull(result);
    }

    @Test
    public void findAllItemsTest() {
        when(itemRepository.findAllByOwnerId(any(Long.class), any(Pageable.class)))
                .thenReturn(new ArrayList<>());

        List<ItemDto> result = itemService.getAllItems(1L, 0, 10);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void findAllByQueryTest() {
        when(itemRepository.findAllByQuery(anyString(), any(Pageable.class)))
                .thenReturn(List.of(item));

        List<ItemDto> result = itemService.findAllByQuery("Item", 0, 10);

        assertNotNull(result);
        assertEquals(result.get(0).getId(), item.getId());
        assertEquals(result.get(0).getName(), item.getName());
    }

    @Test
    public void createCommentExceptionTest() {
        when(itemRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(item));

        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(user));

        when(bookingRepository
                .findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(any(Long.class), any(Long.class),
                        any(Status.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        IllegalArgumentException result = assertThrows(IllegalArgumentException.class,
                () -> itemService.createComment(1L, 1L, commentDto));

        assertNotNull(result);
    }
}