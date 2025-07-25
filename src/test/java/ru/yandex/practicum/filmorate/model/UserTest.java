package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserTest {
    private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private User createValidUser() {
        return User.builder()
                .email("email@mail.ru")
                .login("user")
                .name("name")
                .birthday(LocalDate.of(1985, 5, 3))
                .build();
    }

    @Test
    void shouldCreateValidUser() {
        User user = createValidUser();

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(violations.isEmpty(), "Список нарушений должен быть пустым");
    }

    @Test
    void shouldFailValidationIfEmailIsNull() {
        User user = createValidUser();
        user.setEmail(null);

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(1, violations.size(), "Ожидалось одно нарушение, найдено: " + violations.size());
        assertEquals("Имейл не может быть пустым", violations.iterator().next().getMessage(),
                "Сообщение для вывода: Имейл не может быть пустым");
    }

    @Test
    void shouldFailValidationIfEmailFormatIsIncorrect() {
        User user = createValidUser();
        user.setEmail("@incorrect format.ru");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(1, violations.size(), "Ожидалось одно нарушение, найдено: " + violations.size());
        assertEquals("Неверный формат электронной почты", violations.iterator().next().getMessage(),
                "Сообщение для вывода: Неверный формат электронной почты");
    }

    @Test
    void shouldFailValidationIfLoginIsNull() {
        User user = createValidUser();
        user.setLogin(null);

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(1, violations.size(), "Ожидалось одно нарушение, найдено: " + violations.size());
        assertEquals("Логин не может быть пустым", violations.iterator().next().getMessage(),
                "Сообщение для вывода: Логин не может быть пустым");
    }

    @Test
    void shouldFailValidationIfLoginIsBlank() {
        User user = createValidUser();
        user.setLogin(" ");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(1, violations.size(), "Ожидалось одно нарушение, найдено: " + violations.size());
        assertEquals("Логин не может быть пустым", violations.iterator().next().getMessage(),
                "Сообщение для вывода: Логин не может быть пустым");
    }

    @Test
    void shouldCreateUserIfNameIsEmptyAndSetLoginInstead() {
        User user = createValidUser();
        user.setName(null);

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(violations.isEmpty(), "Список нарушений должен быть пустым");
    }

    @Test
    void shouldFailValidationIfBirthdayIsInFuture() {
        User user = createValidUser();
        user.setBirthday(LocalDate.MAX);

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(1, violations.size(), "Ожидалось одно нарушение, найдено: " + violations.size());
        assertEquals("Дата рождения не может быть в будущем", violations.iterator().next().getMessage(),
                "Сообщение для вывода: Дата рождения не может быть в будущем");
    }

    @Test
    void shouldCreateUserIfBirthdayIsNow() {
        User user = createValidUser();
        user.setBirthday(LocalDate.now());

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(violations.isEmpty(), "Список нарушений должен быть пустым");
    }
}
