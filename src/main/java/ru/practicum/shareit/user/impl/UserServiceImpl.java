package ru.practicum.shareit.user.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Override
    public UserDto add(UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        user = userRepository.save(user);
        log.info("Создан новый пользователь: {}.", user);
        return userMapper.toDto(user);
    }

    @Transactional
    @Override
    public UserDto update(UserDto userDto, long userId) {
        User user = userMapper.toEntity(userDto);
        user = user
                .toBuilder()
                .id(userId)
                .build();
        User savedUser = userRepository
                .findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException(String.format("Пользователь с id = %d не найден.", userId)));
        String name = user.getName() == null ? savedUser.getName() : user.getName();
        String email = user.getEmail() == null ? savedUser.getEmail() : user.getEmail();
        user = User.builder()
                .id(userId)
                .name(name)
                .email(email)
                .build();
        user = userRepository.save(user);
        log.info("Данные пользователя обновлены: {}.", user);
        return userMapper.toDto(user);
    }

    @Transactional(readOnly = true)
    @Override
    public UserDto getById(long userId) {
        log.info(String.format("Запрос на получение пользователя по id: %d.", userId));
        return userMapper.toDto(userRepository
                .findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException(String.format("Пользователь с id = %d не найден.", userId))));
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getAll() {
        log.info("Запрос на получение всех пользователей.");
        return userRepository
                .findAll()
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void deleteById(long userId) {
        checkUserExists(userId);
        userRepository.deleteById(userId);
        log.info(String.format("Пользователь с id = %d удален.", userId));
    }

    @Transactional(readOnly = true)
    @Override
    public void checkUserExists(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(String.format("Пользователь с id = %d не найден.", userId));
        }
    }
}