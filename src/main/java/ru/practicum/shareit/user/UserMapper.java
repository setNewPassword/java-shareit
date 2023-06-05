package ru.practicum.shareit.user;

public interface UserMapper {

    UserDto toDto(User user);

    User toEntity(UserDto userDto);

}
