package ru.practicum.shareit.request;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestDtoTest {
    @Autowired
    JacksonTester<ItemRequestDto> json;

    @Test
    @SneakyThrows
    void itemRequestDtoJsonTest() {
        String description = "looking for a tool to make a hole in the wall with";
        ItemRequestDto itemRequestDto = ItemRequestDto
                .builder()
                .id(1L)
                .description(description)
                .created(LocalDateTime.parse("2023-07-07T15:28:44.11"))
                .build();

        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);

        assertThat(result)
                .extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);
        assertThat(result)
                .extractingJsonPathStringValue("$.description")
                .isEqualTo(description);
        assertThat(result)
                .extractingJsonPathStringValue("$.created")
                .isEqualTo("2023-07-07T15:28:44.11");
    }
}