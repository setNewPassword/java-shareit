package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
public class ItemRequestControllerTest {
    private final String userHeaderId = "X-Sharer-User-Id";

    @MockBean
    private ItemRequestService itemRequestService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;
    private ItemRequestDto itemRequest;

    @BeforeEach
    public void beforeEach() {
        itemRequest = ItemRequestDto
                .builder()
                .id(1L)
                .description("Трям!")
                .build();
    }

    @Test
    @SneakyThrows
    public void createItemRequestTest() {
        ItemRequestDto itemRequestDto = ItemRequestDto
                .builder()
                .description("description")
                .build();
        itemRequest.setDescription("description");

        when(itemRequestService.create(any(Long.class), any(ItemRequestDto.class)))
                .thenReturn(itemRequest);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .header(userHeaderId, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequest.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription()), String.class));

        verify(itemRequestService, times(1))
                .create(any(Long.class), any(ItemRequestDto.class));
    }

    @Test
    @SneakyThrows
    public void findAllItemRequestByUserTest() {
        when(itemRequestService.findAllByUser(any(Long.class)))
                .thenReturn(Collections.emptyList());

        mvc.perform(get("/requests")
                        .header(userHeaderId, 1))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        when(itemRequestService.findAllByUser(any(Long.class)))
                .thenReturn(List.of(itemRequest));

        mvc.perform(get("/requests")
                        .header(userHeaderId, 1))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemRequest))));

        verify(itemRequestService, times(2)).findAllByUser(any(Long.class));
    }

    @Test
    @SneakyThrows
    public void getAllItemRequestByUserTest() {
        when(itemRequestService.getAllByUser(any(Integer.class), any(Integer.class), any(Long.class)))
                .thenReturn(Collections.emptyList());

        mvc.perform(get("/requests/all")
                        .param("from", "0")
                        .param("size", "10")
                        .header(userHeaderId, 1))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        when(itemRequestService.getAllByUser(any(Integer.class), any(Integer.class), any(Long.class)))
                .thenReturn(List.of(itemRequest));

        mvc.perform(get("/requests/all")
                        .param("from", "0")
                        .param("size", "10")
                        .header(userHeaderId, 1))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemRequest))));

        verify(itemRequestService, times(2))
                .getAllByUser(any(Integer.class), any(Integer.class), any(Long.class));
    }


    @Test
    @SneakyThrows
    public void getItemRequestByIdTest() {
        itemRequest.addAllItems(Collections.emptyList());
        when(itemRequestService.getById(any(Long.class), any(Long.class)))
                .thenReturn(itemRequest);

        mvc.perform(get("/requests/1")
                        .header(userHeaderId, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequest.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequest.getDescription()), String.class))
                .andExpect(jsonPath("$.items", is(itemRequest.getItems()), List.class));

        verify(itemRequestService, times(1)).getById(any(Long.class), any(Long.class));
    }
}