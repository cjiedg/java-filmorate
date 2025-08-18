package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.yandex.practicum.filmorate.model.Film.EARLIEST_RELEASE_DATE;

public class FilmTest {
    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private final String formattedDate = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.of("ru")).withZone(ZoneId.systemDefault()).format(EARLIEST_RELEASE_DATE);

    private Film createValidFilm() {
        return Film.builder()
                .name("Valid film")
                .description("Valid description")
                .releaseDate(LocalDate.of(2012, 12, 5))
                .duration(Duration.ofMinutes(45))
                .build();
    }

    @Test
    void shouldCreateValidFilm() {
        Film film = createValidFilm();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertTrue(violations.isEmpty(), "Список нарушений должен быть пустым");
    }

    @Test
    void shouldFailValidationIfNameIsBlank() {
        Film film = createValidFilm();
        film.setName(" ");

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(1, violations.size(), "Ожидалось одно нарушение, найдено: " + violations.size());
        assertEquals("Название фильма не может быть пустым", violations.iterator().next().getMessage(), "Название фильма не может быть пустым");
    }

    @Test
    void shouldFailValidationIfNameIsNull() {
        Film film = createValidFilm();
        film.setName(null);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(1, violations.size(), "Ожидалось одно нарушение, найдено: " + violations.size());
        assertEquals("Название фильма не может быть пустым", violations.iterator().next().getMessage(), "Название фильма не может быть пустым");
    }

    @Test
    void shouldPassValidationIfDescriptionLengthIs200Symbols() {
        Film film = createValidFilm();
        film.setDescription("a".repeat(Film.MAX_DESCRIPTION_LENGTH));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertTrue(violations.isEmpty(), "Список нарушений должен быть пустым");
    }

    @Test
    void shouldFailValidationIfDescriptionLengthIsMoreThan200Symbols() {
        Film film = createValidFilm();
        film.setDescription("a".repeat(Film.MAX_DESCRIPTION_LENGTH + 1));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(1, violations.size(), "Ожидалось одно нарушение, найдено: " + violations.size());
        assertEquals("Максимальная длина описания - 200 символов", violations.iterator().next().getMessage(),
                "Сообщение для вывода: Максимальная длина описания - 200 символов");
    }

    @Test
    void shouldFailValidationIfReleaseDateIsEarlierThanEarliestReleaseDate() {
        Film film = createValidFilm();
        film.setReleaseDate(LocalDate.of(1895, 6, 3));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(1, violations.size(), "Ожидалось одно нарушение, найдено: " + violations.size());
        assertEquals("Дата релиза не может быть раньше " + formattedDate, violations.iterator().next().getMessage(),
                "Сообщение для вывода: Дата релиза не может быть раньше " + formattedDate);
    }

    @Test
    void shouldFailValidationIfDurationIsNegative() {
        Film film = createValidFilm();
        film.setDuration(Duration.ofMinutes(-3));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(1, violations.size(), "Ожидалось одно нарушение, найдено: " + violations.size());
        assertEquals("Продолжительность фильма должна быть положительным числом", violations.iterator().next().getMessage(),
                "Сообщение для вывода: Продолжительность фильма должна быть положительным числом");
    }

    @Test
    void shouldFailValidationIfDurationIsZero() {
        Film film = createValidFilm();
        film.setDuration(Duration.ofMinutes(0));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(1, violations.size(), "Ожидалось одно нарушение, найдено: " + violations.size());
        assertEquals("Продолжительность фильма должна быть положительным числом", violations.iterator().next().getMessage(),
                "Сообщение для вывода: Продолжительность фильма должна быть положительным числом");
    }
}
