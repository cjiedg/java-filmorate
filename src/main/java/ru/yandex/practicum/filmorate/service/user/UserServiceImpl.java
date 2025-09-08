package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    @Override
    public User getUserById(Long id) {
        validateId(id);
        return getUserOrThrow(id);
    }

    @Override
    public User create(User user) {
        if (userStorage.existsByEmail(user.getEmail())) {
            String msg = "Email уже используется";
            log.warn(msg);
            throw new ValidationException(msg);
        }
        return userStorage.create(user);
    }

    @Override
    public User update(User newUser) {
        Long newUserId = newUser.getId();
        User oldUser = getUserOrThrow(newUserId);
        if (!(oldUser.getEmail().equals(newUser.getEmail()))
                && userStorage.existsByEmail(newUser.getEmail())) {
            String msg = "Email уже используется";
            log.warn(msg);
            throw new ValidationException(msg);
        }
        return userStorage.update(newUser);
    }

    @Override
    public void delete(long id) {
        validateId(id);
        User user = getUserOrThrow(id);
        userStorage.delete(user);
    }

    @Override
    public void addFriend(long userId, long friendId) {
        if (userId == friendId) {
            String msg = "Нельзя добавить самого себя в друзья";
            log.warn(msg);
            throw new ValidationException(msg);
        }
        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);

        if (user.getFriends().contains(friendId)) {
            String msg = "Пользователь уже в друзьях";
            log.warn(msg);
            throw new ValidationException(msg);
        }

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    @Override
    public List<User> getFriends(long userId) {
        User user = getUserOrThrow(userId);
        Set<Long> friendsId = user.getFriends();
        if (user.getFriends().isEmpty()) {
            return Collections.emptyList();
        }
        return friendsId.stream()
                .map(this::getUserOrThrow)
                .collect(Collectors.toList()
                );
    }

    @Override
    public List<User> getCommonFriends(long userId1, long userId2) {
        Set<Long> friends1 = getUserOrThrow(userId1).getFriends();
        Set<Long> friends2 = getUserOrThrow(userId2).getFriends();

        return friends1.stream()
                .filter(friends2::contains)
                .map(this::getUserOrThrow)
                .collect(Collectors.toList()
                );
    }

    private void validateId(Long id) throws ValidationException {
        if (id == null) {
            String msg = "Id должен быть указан";
            log.warn(msg);
            throw new ValidationException(msg);
        }
        if (id <= 0) {
            String msg = "Id должен быть положительным числом";
            log.warn(msg);
            throw new ValidationException(msg);
        }
    }

    private User getUserOrThrow(Long userId) {
        return userStorage.getUserById(userId)
                .orElseThrow(() -> {
                    String msg = "Пользователь с id = " + userId + " не найден";
                    log.warn(msg);

                    throw new NotFoundException(msg);
                });
    }
}
