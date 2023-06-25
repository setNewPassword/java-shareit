package ru.practicum.shareit.user;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
@Service
public interface UserMapper {
    UserDto toDto(User user);

    User toEntity(UserDto userDto);
}
