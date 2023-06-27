package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    UserDto add(UserDto userDto);

    UserDto update(UserDto userDto, long id);

    UserDto getById(long userId);

    List<UserDto> getAll();

    void deleteById(long userId);

    void checkUserExists(long userId);
}