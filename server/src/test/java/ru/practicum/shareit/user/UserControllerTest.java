package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
public class UserControllerTest {
    @MockBean
    private UserService userService;
    private final ObjectMapper mapper = new ObjectMapper();
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    @Autowired
    private MockMvc mvc;
    private User homerSimpson;
    private UserDto homerDto;

    @BeforeEach
    public void beforeEach() {
        homerSimpson = User
                .builder()
                .name("Homer Simpson")
                .email("homer@springfield.net")
                .build();

        homerDto = userMapper.toDto(homerSimpson);
    }

    @Test
    @SneakyThrows
    public void createUserTest() {
        long userId = 1;
        homerSimpson.setId(userId);

        when(userService.add(any(UserDto.class)))
                .thenReturn(userMapper.toDto(homerSimpson));

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(homerSimpson))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json(mapper.writeValueAsString(homerSimpson)));

        verify(userService, times(1)).add(any(UserDto.class));
    }

    @Test
    @SneakyThrows
    public void updateUserTest() throws Exception {
        long userId = 1;
        homerSimpson.setId(userId);
        homerSimpson.setName("Bart Simpson");
        UserDto bartDto = userMapper.toDto(homerSimpson);

        when(userService.update(any(UserDto.class), anyLong()))
                .thenReturn(bartDto);

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(bartDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bartDto)));

        verify(userService, times(1)).update(any(UserDto.class), anyLong());
    }

    @Test
    @SneakyThrows
    public void getUserTest() {
        long userId = 1;
        homerDto.setId(userId);

        when(userService.getById(userId))
                .thenReturn(homerDto);

        mvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(homerDto)));

        verify(userService, times(1)).getById(userId);
    }

    @Test
    @SneakyThrows
    public void findAllUsersTest() {
        when(userService.getAll())
                .thenReturn(Collections.emptyList());
        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        when(userService.getAll())
                .thenReturn(List.of(homerDto));
        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(homerDto))));

        verify(userService, times(2)).getAll();
    }

    @Test
    @SneakyThrows
    public void removeTest() throws Exception {
        mvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteById(any(Long.class));
    }
}