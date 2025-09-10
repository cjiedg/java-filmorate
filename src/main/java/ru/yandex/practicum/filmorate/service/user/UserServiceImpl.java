package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.user.UserStorage;
import ru.yandex.practicum.filmorate.dto.User.CreateUserRequest;
import ru.yandex.practicum.filmorate.dto.User.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.User.UserDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Override
    public Collection<UserDto> findAll() {
        List<User> allUsers = new ArrayList<>(userStorage.findAll());
        Map<Long, User> allUsersMap = allUsers.stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        return allUsers.stream()
                .map(u -> UserMapper.mapToUserDto(u, allUsersMap))
                .collect(Collectors.toList());
    }


    @Override
    public UserDto getUserById(Long id) {
        validateId(id);
        User user = getUserOrThrow(id);
        Map<Long, User> allUsersMap = userStorage.findAll().stream()
                .collect(Collectors.toMap(User::getId, u -> u));
        return UserMapper.mapToUserDto(user, allUsersMap);
    }


    @Override
    public UserDto create(CreateUserRequest request) {
        if (request.getBirthday() == null)
            throw new ValidationException("Дата рождения обязательна");
        if (userStorage.existsByEmail(request.getEmail()))
            throw new ValidationException("Email уже используется");

        User user = UserMapper.mapToUser(request);
        User createdUser = userStorage.create(user);


        Map<Long, User> allUsersMap = userStorage.findAll().stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        return UserMapper.mapToUserDto(createdUser, allUsersMap);
    }

    @Override
    public UserDto update(UpdateUserRequest request) {
        Long id = request.getId();
        User existingUser = getUserOrThrow(id);

        if (!existingUser.getEmail().equals(request.getEmail()) && userStorage.existsByEmail(request.getEmail())) {
            throw new ValidationException("Email уже используется");
        }

        User updatedUser = UserMapper.updateUserFields(existingUser, request);
        User savedUser = userStorage.update(updatedUser);


        Map<Long, User> allUsersMap = userStorage.findAll().stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        return UserMapper.mapToUserDto(savedUser, allUsersMap);
    }

    @Override
    public void delete(long id) {
        validateId(id);
        User user = getUserOrThrow(id);
        userStorage.delete(user);
    }

    @Override
    public void addFriend(long userId, long friendId) {
        validateId(userId);
        validateId(friendId);

        if (userId == friendId)
            throw new ValidationException("Нельзя добавить самого себя в друзья");

        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);


        userStorage.addFriend(userId, friendId);


    }


    @Override
    public void confirmFriendship(long userId, long friendId) {
        validateId(userId);
        validateId(friendId);


        userStorage.confirmFriend(userId, friendId);
    }


    @Override
    public void removeFriend(long userId, long friendId) {
        validateId(userId);
        validateId(friendId);


        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);

        userStorage.removeFriend(userId, friendId);
    }

    @Override
    public List<UserDto> getFriends(long userId) {
        User user = getUserOrThrow(userId);


        List<User> friends = userStorage.getFriends(userId);

        Map<Long, User> allUsersMap = userStorage.findAll().stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        return friends.stream()
                .map(f -> UserMapper.mapToUserDto(f, allUsersMap))
                .collect(Collectors.toList());
    }


    @Override
    public List<UserDto> getCommonFriends(long userId1, long userId2) {
        validateId(userId1);
        validateId(userId2);

        List<User> commonFriends = userStorage.getCommonFriends(userId1, userId2);

        Map<Long, User> allUsersMap = userStorage.findAll().stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        return commonFriends.stream()
                .map(f -> UserMapper.mapToUserDto(f, allUsersMap))
                .collect(Collectors.toList());
    }


    private void validateId(Long id) {
        if (id == null || id <= 0) throw new ValidationException("Id должен быть положительным числом");
    }

    private User getUserOrThrow(Long userId) {
        return userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
    }
}
