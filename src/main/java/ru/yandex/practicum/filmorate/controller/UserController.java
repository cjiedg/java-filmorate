package ru.yandex.practicum.filmorate.controller;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public Collection<User> findAll() {
        log.info("Запрос на получение всех пользователей");
        Collection<User> allUsers = userService.findAll();
        log.debug("Найдено пользователей: {}", allUsers.size());
        return allUsers;
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable @Positive Long id) {
        log.info("Запрос на получение пользователя с id = {}", id);
        User foundUser = userService.getUserById(id);
        log.debug("Найден пользователь с id = {}", id);
        return foundUser;
    }

    @PostMapping
    public User create(@RequestBody @Valid User user) {
        log.info("Запрос на создание пользователя {}", user);
        User createdUser = userService.create(user);
        log.debug("Создан пользователь с id = {}", user.getId());
        return createdUser;
    }

    @PutMapping
    public User update(@RequestBody @Valid User newUser) {
        log.info("Запрос на обновление пользователя {}", newUser);
        User updatedUser = userService.update(newUser);
        log.debug("Фильм с id = {} успешно обновлён: {}", updatedUser.getId(), updatedUser);
        return updatedUser;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable @Positive Long id) {
        log.info("Запрос на удаление пользователя с id = {}", id);
        userService.delete(id);
        log.debug("Удалён пользователь с id = {}", id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id,
                          @PathVariable Long friendId) {
        log.info("Запрос на добавление в друзья пользователей с id {} (отправитель) и {} (получатель)", id, friendId);
        userService.addFriend(id, friendId);
        log.debug("Отправлен запрос на добавление в друзья пользователей с id {} (отправитель) и {} (получатель)", id, friendId);
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
        log.info("Запрос на удаление из друзей пользователей с id {} (отправитель) и {}", id, friendId);
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

    @PostMapping("/{id}/friends/{friendId}/confirm")
    public void confirmFriendship(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Подтверждение запроса о добавлении в друзья пользователя с id {} (отправитель) полльзователю {} (получатель)", id, friendId);
        userService.confirmFriendship(id, friendId);
        log.debug("Пользователи с id {} и {} добавлены друг другу в друзья", id, friendId);
    }
}
