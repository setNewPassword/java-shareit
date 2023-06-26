package ru.practicum.shareit.user.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EmailAlreadyExistException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User add(User user) {
        user = userRepository.save(user);
        log.info("Создан новый пользователь: {}.", user);
        return user;
    }

    @Transactional
    @Override
    public User update(User user) {
        checkUserIsRegistered(user);
        long userId = user.getId();
        User savedUser = getById(userId);
        String name = user.getName() == null ? savedUser.getName() : user.getName();
        String email = user.getEmail() == null ? savedUser.getEmail() : user.getEmail();
        user = User.builder()
                .id(userId)
                .name(name)
                .email(email)
                .build();
        user = userRepository.save(user);
        log.info("Данные пользователя обновлены: {}.", user);
        return user;
    }

    @Transactional(readOnly = true)
    @Override
    public User getById(long userId) {
        log.info(String.format("Запрос на получение пользователя по id: %d.", userId));
        return userRepository
                .findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException(String.format("Пользователь с id = %d не найден.", userId)));
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> getAll() {
        log.info("Запрос на получение всех пользователей.");
        return userRepository.findAll();
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

    private void checkUserIsRegistered(User user) {
        Optional<User> registeredUser = userRepository.findUserByEmail(user.getEmail());
        if (registeredUser.isPresent() && registeredUser.get().getId() != user.getId()) {
            throw new EmailAlreadyExistException(
                    String.format("Пользователь с Email: %s уже зарегистрирован.", user.getEmail()));
        }
    }
}