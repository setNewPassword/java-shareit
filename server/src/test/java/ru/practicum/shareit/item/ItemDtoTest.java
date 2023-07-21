package ru.practicum.shareit.item;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoTest {
    @Autowired
    JacksonTester<ItemDto> json;

    @Test
    @SneakyThrows
    void itemDtoJsonTest() {
        ItemDto itemDto = ItemDto
                .builder()
                .id(1L)
                .name("Screwdriver")
                .description("tool, usually hand-operated, for turning screws with slotted heads")
                .available(true)
                .build();

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result)
                .extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);
        assertThat(result)
                .extractingJsonPathStringValue("$.name")
                .isEqualTo("Screwdriver");
        assertThat(result)
                .extractingJsonPathStringValue("$.description")
                .isEqualTo("tool, usually hand-operated, for turning screws with slotted heads");
        assertThat(result)
                .extractingJsonPathBooleanValue("$.available")
                .isEqualTo(true);
    }
}