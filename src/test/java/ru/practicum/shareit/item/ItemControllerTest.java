package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
public class ItemControllerTest {
    @MockBean
    private ItemService itemService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    private ItemDto itemDto, inputDto;

    @BeforeEach
    public void beforeEach() {
        itemDto = ItemDto
                .builder()
                .id(1L)
                .name("Screwdriver")
                .description("tool, usually hand-operated, for turning screws with slotted heads")
                .available(true)
                .build();

        inputDto = ItemDto
                .builder()
                .name("Screwdriver")
                .description("tool, usually hand-operated, for turning screws with slotted heads")
                .available(true)
                .build();
    }

    @Test
    @SneakyThrows
    public void createTest() {
        when(itemService.add(any(ItemDto.class), any(Long.class)))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(inputDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription()), String.class));

        verify(itemService, times(1))
                .add(any(ItemDto.class), any(Long.class));
    }

    @Test
    @SneakyThrows
    public void createCommentTest() {
        CommentDto inputCommentDto = CommentDto
                .builder()
                .text("It's awesome!")
                .build();
        CommentDto responseCommentDto = CommentDto
                .builder()
                .id(1L)
                .text("It's awesome!")
                .build();

        when(itemService.createComment(any(Long.class), any(Long.class), any(CommentDto.class)))
                .thenReturn(responseCommentDto);


        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(inputCommentDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseCommentDto.getId()), Long.class))
                .andExpect(jsonPath("$.authorName", is(responseCommentDto.getAuthorName()), String.class))
                .andExpect(jsonPath("$.text", is(responseCommentDto.getText()), String.class));

        verify(itemService, times(1))
                .createComment(any(Long.class), any(Long.class), any(CommentDto.class));
    }

    @Test
    @SneakyThrows
    public void updateTest() {
        inputDto = inputDto
                .toBuilder()
                .name("Scissors")
                .build();
        itemDto = itemDto.toBuilder()
                .name("Scissors")
                .build();

        when(itemService.update(any(ItemDto.class), any(Long.class), any(Long.class)))
                .thenReturn(itemDto);

        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(inputDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class));

        verify(itemService, times(1))
                .update(any(ItemDto.class), any(Long.class), any(Long.class));
    }


    @Test
    @SneakyThrows
    public void getItemTest() {
        when(itemService.getById(any(Long.class), any(Long.class)))
                .thenReturn(itemDto);

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class));

        verify(itemService, times(1))
                .getById(any(Long.class), any(Long.class));
    }

    @Test
    @SneakyThrows
    public void returnUserItemsTest() {
        when(itemService.getAllItems(any(Long.class), any(Integer.class), any(Integer.class)))
                .thenReturn(new ArrayList<>());

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(itemService, times(1))
                .getAllItems(any(Long.class), any(Integer.class), any(Integer.class));
    }

    @Test
    @SneakyThrows
    public void returnByQueryTest() throws Exception {
        when(itemService.findAllByQuery(any(String.class), any(Integer.class), any(Integer.class)))
                .thenReturn(new ArrayList<>());

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1)
                        .param("text", "any text")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(itemService, times(1))
                .findAllByQuery(any(String.class), any(Integer.class), any(Integer.class));
    }

    @Test
    @SneakyThrows
    public void missingHeaderTest() {
        when(itemService.getById(any(Long.class), any(Long.class)))
                .thenReturn(itemDto);

        mvc.perform(get("/items/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Параметр UserID не указан в заголовке HTTP-запроса.")));

        verifyNoInteractions(itemService);
    }

}