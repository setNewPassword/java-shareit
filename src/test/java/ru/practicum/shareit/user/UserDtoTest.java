package ru.practicum.shareit.user;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UserDtoTest {
    @Autowired
    JacksonTester<UserDto> json;

    @Test
    @SneakyThrows
    void userDtoJsonTest() {
        UserDto homerDto = UserDto
                .builder()
                .id(1L)
                .name("Homer Simpson")
                .email("homer@springfield.net")
                .build();

        JsonContent<UserDto> result = json.write(homerDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(((int) homerDto.getId()));
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(homerDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo(homerDto.getEmail());
    }
}