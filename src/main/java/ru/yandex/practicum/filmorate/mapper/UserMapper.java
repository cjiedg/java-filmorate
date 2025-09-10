package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.User.CreateUserRequest;
import ru.yandex.practicum.filmorate.dto.User.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.User.UserDto;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class UserMapper {

    public static UserDto mapToUserDto(User user, Map<Long, User> allUsersMap) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setLogin(user.getLogin());
        dto.setName(user.getName());
        dto.setBirthday(user.getBirthday());

        if (user.getFriends() != null) {
            Set<UserDto> friendDtos = user.getFriends().entrySet().stream()
                    .filter(Map.Entry::getValue)
                    .map(Map.Entry::getKey)
                    .map(allUsersMap::get)
                    .filter(Objects::nonNull)
                    .map(UserMapper::mapToShallowDto)
                    .collect(Collectors.toSet());
            dto.setFriends(friendDtos);
        }

        return dto;
    }

    public static UserDto mapToShallowDto(User user) {
        UserDto d = new UserDto();
        d.setId(user.getId());
        d.setEmail(user.getEmail());
        d.setLogin(user.getLogin());
        d.setName(user.getName());
        d.setBirthday(user.getBirthday());

        return d;
    }


    public static User mapToUser(CreateUserRequest request) {
        return User.builder()
                .email(request.getEmail())
                .login(request.getLogin())
                .name(request.getName())
                .birthday(request.getBirthday())
                .build();
    }

    public static User updateUserFields(User existing, UpdateUserRequest request) {
        existing.setEmail(request.getEmail());
        existing.setLogin(request.getLogin());
        existing.setName(request.getName());
        existing.setBirthday(request.getBirthday());
        return existing;
    }
}
