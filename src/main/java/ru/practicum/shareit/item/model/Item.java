package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Value;
import ru.practicum.shareit.user.model.User;

@Value
@Builder(toBuilder = true)
public class Item {
    long id;
    String name;
    String description;
    Boolean available;
    User owner;
}
