package ru.yandex.practicum.filmorate.service.user;

import ru.yandex.practicum.filmorate.dto.User.CreateUserRequest;
import ru.yandex.practicum.filmorate.dto.User.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.User.UserDto;

import java.util.Collection;
import java.util.List;

public interface UserService {
    void addFriend(long userId, long friendId);

    void removeFriend(long userId, long friendId);

    List<UserDto> getFriends(long userId);

    List<UserDto> getCommonFriends(long userId1, long userId2);

    Collection<UserDto> findAll();

    UserDto getUserById(Long id);

    UserDto create(CreateUserRequest request);

    UserDto update(UpdateUserRequest request);

    void delete(long id);

    void confirmFriendship(long userId, long friendId);
}
