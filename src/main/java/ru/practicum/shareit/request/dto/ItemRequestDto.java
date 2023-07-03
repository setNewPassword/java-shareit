package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

//@Value
@Builder(toBuilder = true)
@Setter
@Getter
public class ItemRequestDto {
    long id;
    @NotBlank
    String description;

    LocalDateTime created;

    List<ItemDto> items;

}