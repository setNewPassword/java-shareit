package ru.practicum.shareit.user.dto;

import lombok.*;

@Builder(toBuilder = true)
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    long id;

    String name;

    String email;
}