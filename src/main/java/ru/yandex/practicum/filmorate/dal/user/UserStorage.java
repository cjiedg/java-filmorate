package ru.yandex.practicum.filmorate.dal.user;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserStorage {
    Collection<User> findAll();

    User create(User user);

    User update(User newUser) throws ValidationException;

    Optional<User> getUserById(long id);

    void delete(User user);

    boolean existsByEmail(String email);

    void removeFriend(long userId, long friendId);

    void confirmFriend(long userId, long friendId);

    void addFriend(long userId, long friendId);

    List<User> getFriends(long userId);

    Set<Long> getFriendsIds(long userId);

    List<User> getCommonFriends(long userId1, long userId2);
}
