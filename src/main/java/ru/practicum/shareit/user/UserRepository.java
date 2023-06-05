package ru.practicum.shareit.user;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);

    Optional<User> getById(long userId);

    List<User> getAll();

    void deleteById(long userId);

    boolean existsById(long userId);

    Optional<Long> findUserIdByEmail(String email);
}
