package ru.yandex.practicum.filmorate.controller;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.User.CreateUserRequest;
import ru.yandex.practicum.filmorate.dto.User.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.User.UserDto;
import ru.yandex.practicum.filmorate.service.user.UserService;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public Collection<UserDto> findAll() {
        log.info("Запрос на получение всех пользователей");
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable @Positive Long id) {
        log.info("Запрос на получение пользователя с id = {}", id);
        return userService.getUserById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@RequestBody @Valid CreateUserRequest request) {
        log.info("Запрос на создание пользователя {}", request);
        return userService.create(request);
    }

    @PutMapping
    public UserDto update(@RequestBody @Valid UpdateUserRequest request) {
        log.info("Запрос на обновление пользователя {}", request);
        return userService.update(request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long id) {
        log.info("Запрос на удаление пользователя с id = {}", id);
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity<Void> addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Добавление в друзья пользователей {} и {}", id, friendId);
        userService.addFriend(id, friendId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity<Void> removeFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Удаление дружбы между {} и {}", id, friendId);
        userService.removeFriend(id, friendId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/friends")
    public List<UserDto> getFriends(@PathVariable Long id) {
        log.info("Получение друзей пользователя {}", id);
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<UserDto> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        log.info("Получение общих друзей пользователей {} и {}", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }

    @PutMapping("/{id}/friends/{friendId}/confirm")
    public ResponseEntity<Void> confirmFriendship(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Подтверждение дружбы между {} и {}", id, friendId);
        userService.confirmFriendship(id, friendId);
        return ResponseEntity.ok().build();
    }
}
