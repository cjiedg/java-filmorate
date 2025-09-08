package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    Collection<User> findAll();

    User create(User user);

    User update(User newUser) throws ValidationException;

    Optional<User> getUserById(long id);

    void delete(User user);

    boolean existsByEmail(String email);
}
