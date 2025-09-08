package ru.yandex.practicum.filmorate.dto.User;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateUserRequest {
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
}