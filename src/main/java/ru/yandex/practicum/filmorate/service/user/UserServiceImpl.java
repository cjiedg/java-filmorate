package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public void addFriend(long userId, long friendId) {
        if (userId == friendId) {
            String msg = "Нельзя добавить самого себя в друзья";
            log.warn(msg);
            throw new ValidationException(msg);
        }
        User user = userStorage.findById(userId);
        User friend = userStorage.findById(friendId);

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
        User user = userStorage.findById(userId);
        User friend = userStorage.findById(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    @Override
    public List<User> getFriends(long userId) {
        User user = userStorage.findById(userId);
        Set<Long> friendsId = user.getFriends();
        if (user.getFriends().isEmpty()) {
            return Collections.emptyList();
        }
        return friendsId.stream()
                .map(userStorage::findById)
                .collect(Collectors.toList()
                );
    }

    @Override
    public List<User> getCommonFriends(long userId1, long userId2) {
        Set<Long> friends1 = userStorage.findById(userId1).getFriends();
        Set<Long> friends2 = userStorage.findById(userId2).getFriends();

        return friends1.stream()
                .filter(friends2::contains)
                .map(userStorage::findById)
                .collect(Collectors.toList()
                );
    }
}
