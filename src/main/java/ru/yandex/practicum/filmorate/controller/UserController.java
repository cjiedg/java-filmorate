package ru.yandex.practicum.filmorate.controller;


import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();

    @GetMapping
    public Collection<User> findAll() {
        log.info("Запрос на получение всех пользователей");
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody @Valid User user) {
        log.info("Запрос на создание пользователя {}", user);
        if (emails.contains(user.getEmail())) {
            String msg = "Этот имейл уже используется";
            log.warn(msg);
            throw new ValidationException(msg);
        }
        user.setId(generateNextId());
        user.setName(user.getName() == null ? user.getLogin() : user.getName());
        emails.add(user.getEmail());
        users.put(user.getId(), user);
        log.debug("Создан пользователь с id = {}", user.getId());
        return user;
    }

    @PutMapping
    public User update(@RequestBody @Valid User newUser) {
        log.info("Запрос на обновление пользователя {}", newUser);
        if (newUser.getId() == null) {
            String msg = "Id должен быть указан";
            log.warn(msg);
            throw new ValidationException(msg);
        }
        Long newUserId = newUser.getId();
        if (users.containsKey(newUserId)) {
            User oldUser = users.get(newUserId);
            boolean isDuplicated = !(oldUser.getEmail().equals(newUser.getEmail()))
                    && emails.contains(newUser.getEmail());
            if (isDuplicated) {
                String msg = "Этот имейл уже используется";
                log.warn(msg);
                throw new ValidationException(msg);
            }
            if (newUser.getEmail() != null && !newUser.getEmail().equals(oldUser.getEmail())) {
                emails.remove(oldUser.getEmail());
                emails.add(newUser.getEmail());
            }
            log.trace("Обновление полей пользователя с id = {}", newUserId);
            oldUser.setEmail(Objects.requireNonNullElse(newUser.getEmail(), oldUser.getEmail()));
            oldUser.setLogin(Objects.requireNonNullElse(newUser.getLogin(), oldUser.getLogin()));
            oldUser.setName(Objects.requireNonNullElse(newUser.getName(), oldUser.getName()));
            oldUser.setBirthday(Objects.requireNonNullElse(newUser.getBirthday(), oldUser.getBirthday()));
            log.debug("Фильм с id = {} успешно обновлён: {}", newUserId, newUser);
            return oldUser;
        }
        String msg = "Пользователь с id = " + newUserId + " не найден";
        log.warn(msg);
        throw new ValidationException(msg);

    }


    private Long generateNextId() {
        return users.keySet().stream()
                .max(Long::compareTo)

                .orElse(0L) + 1;
    }
}
