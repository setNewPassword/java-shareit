package ru.practicum.shareit.user.impl;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private static long counter;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User save(User user) {
        if (user.getId() == 0) {
            user = user
                    .toBuilder()
                    .id(++counter)
                    .build();
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> getById(long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public List<User> getAll() {
        return users.values()
                .stream()
                .sorted(Comparator.comparingLong(User::getId))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(long userId) {
        users.remove(userId);
    }

    @Override
    public boolean existsById(long userId) {
        return users.get(userId) != null;
    }

    @Override
    public Optional<Long> findUserIdByEmail(String email) {
        return users.values()
                .stream()
                .filter(user -> user.getEmail().equals(email))
                .map(User::getId)
                .findAny();
    }
}