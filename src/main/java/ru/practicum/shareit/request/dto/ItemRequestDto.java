package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder(toBuilder = true)
@Setter
@Getter
public class ItemRequestDto {
    long id;
    @NotBlank
    String description;

    LocalDateTime created;

    final List<ItemDto> items = new ArrayList<>();

    public void addAllItems(List<ItemDto> itemDtoList) {
        items.addAll(itemDtoList);
    }

}