package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserMapperTest {
    private User homerSimpson;
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @BeforeEach
    public void beforeEach() {
        homerSimpson = User
                .builder()
                .id(1L)
                .name("Homer Simpson")
                .email("homer@springfield.net")
                .build();
    }

    @Test
    public void toUserDtoTest() {
        UserDto homerDto = userMapper.toDto(homerSimpson);

        assertEquals(homerSimpson.getId(), homerDto.getId());
        assertEquals(homerSimpson.getName(), homerDto.getName());
        assertEquals(homerSimpson.getEmail(), homerDto.getEmail());
    }

    @Test
    public void toUserModelTest() {
        UserDto homerDto = UserDto
                .builder()
                .id(1L)
                .name("Homer Simpson")
                .email("homer@springfield.net")
                .build();
        User newUser = userMapper.toEntity(homerDto);

        assertEquals(homerDto.getId(), newUser.getId());
        assertEquals(homerDto.getName(), newUser.getName());
        assertEquals(homerDto.getEmail(), newUser.getEmail());
    }

}