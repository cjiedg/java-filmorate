package ru.yandex.practicum.filmorate.controller;


import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserStorage userStorage;
    private final UserService userService;

    @Autowired
    public UserController(UserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> findAll() {
        log.info("Запрос на получение всех пользователей");
        Collection<User> allUsers = userStorage.findAll();
        log.debug("Найдено пользователей: {}", allUsers.size());
        return allUsers;
    }

    @GetMapping("/{id}")
    public User findUser(@PathVariable Long id) {
        log.info("Запрос на получение пользователя с id = {}", id);
        User foundUser = userStorage.findById(id);
        log.debug("Найден пользователь с id = {}", id);
        return foundUser;
    }

    @PostMapping
    public User create(@RequestBody @Valid User user) {
        log.info("Запрос на создание пользователя {}", user);
        User createdUser = userStorage.create(user);
        log.debug("Создан пользователь с id = {}", user.getId());
        return createdUser;
    }

    @PutMapping
    public User update(@RequestBody @Valid User newUser) {
        log.info("Запрос на обновление пользователя {}", newUser);
        User updatedUser = userStorage.update(newUser);
        log.debug("Фильм с id = {} успешно обновлён: {}", updatedUser.getId(), updatedUser);
        return updatedUser;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id,
                          @PathVariable Long friendId) {
        log.info("Запрос на добавление в друзья пользователей с id {} и {}", id, friendId);
        userService.addFriend(id, friendId);
        log.debug("Пользователи с id {} и {} добавлены друг другу в друзья", id, friendId);
    }

    @GetMapping("{id}/friends")
    public Collection<User> findFriends(@PathVariable Long id) {
        log.info("Запрос на получение всех друзей пользователя с id = {}", id);
        Collection<User> allFriends = userService.getFriends(id);
        log.debug("Количество друзей пользователя с id = {}: {}", id, allFriends.size());
        return allFriends;
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable Long id,
                             @PathVariable Long friendId) {
        log.info("Запрос на удаление из друзей пользователей с id {} и {}", id, friendId);
        userService.removeFriend(id, friendId);
        log.debug("Пользователи с id {} и {} больше не друзья", id, friendId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> findCommonFriends(
            @PathVariable Long id,
            @PathVariable Long otherId) {
        log.info("Запрос на получение списка общих друзей пользователей с id = {} и {}", id, otherId);
        Collection<User> commonFriends = userService.getCommonFriends(id, otherId);
        log.debug("Количество общих друзей пользователей с id = {} и {}: {}", id, otherId, commonFriends.size());
        return commonFriends;
    }
}
