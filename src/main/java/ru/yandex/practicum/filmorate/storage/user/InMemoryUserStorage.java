package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User create(User user) throws ValidationException {
        Long id = generateNextId();
        user.setId(id);
        user.setName(user.getName() == null ? user.getLogin() : user.getName());
        emails.add(user.getEmail());
        users.put(id, user);
        return users.get(id);
    }

    @Override
    public User update(User newUser) throws ValidationException {
        Long newUserId = newUser.getId();
        User oldUser = getUserById(newUserId)
                .orElseThrow(() -> {
                    String msg = "Пользователь с id = " + newUserId + " не найден";
                    log.warn(msg);

                    throw new NotFoundException(msg);
                });
        if (newUser.getEmail() != null && !newUser.getEmail().equals(oldUser.getEmail())) {
            emails.remove(oldUser.getEmail());
            emails.add(newUser.getEmail());
        }
        log.trace("Обновление полей пользователя с id = {}", newUserId);
        oldUser.setEmail(Objects.requireNonNullElse(newUser.getEmail(), oldUser.getEmail()));
        oldUser.setLogin(Objects.requireNonNullElse(newUser.getLogin(), oldUser.getLogin()));
        oldUser.setName(Objects.requireNonNullElse(newUser.getName(), oldUser.getName()));
        oldUser.setBirthday(Objects.requireNonNullElse(newUser.getBirthday(), oldUser.getBirthday()));
        return oldUser;
    }

    @Override
    public Optional<User> getUserById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public void delete(User user) {
        emails.remove(user.getEmail());
        users.remove(user.getId());
    }

    @Override
    public boolean existsByEmail(String email) {
        return emails.contains(email);
    }

    private Long generateNextId() {
        return users.keySet().stream()
                .max(Long::compareTo)
                .orElse(0L) + 1;
    }
}
