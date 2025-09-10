package ru.yandex.practicum.filmorate.dto.Film;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.EqualOrAfter;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.List;

@Data
public class UpdateFilmRequest {

    @NotNull(message = "Id обязателен для обновления")
    private Long id;

    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;

    @Size(max = Film.MAX_DESCRIPTION_LENGTH,
            message = "Максимальная длина описания - " + Film.MAX_DESCRIPTION_LENGTH + " символов")
    private String description;

    @NotNull(message = "Дата релиза обязательна")
    @EqualOrAfter(message = "Дата релиза не может быть раньше 28.12.1895")
    private LocalDate releaseDate;

    @NotNull(message = "Продолжительность обязательна")
    @Positive(message = "Продолжительность должна быть положительным числом")
    private Long duration;

    @NotNull(message = "Рейтинг (MPA) обязателен")
    private Mpa mpa;

    private List<Genre> genres;
}
