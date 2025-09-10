package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.annotation.EqualOrAfter;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Data
@EqualsAndHashCode(of = "id")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Film {
    public static final LocalDate EARLIEST_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    public static final int MAX_DESCRIPTION_LENGTH = 200;

    private Long id;

    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;

    @Size(max = MAX_DESCRIPTION_LENGTH, message = "Максимальная длина описания - " + MAX_DESCRIPTION_LENGTH + " символов")
    private String description;

    @NotNull(message = "Продолжительность обязательна")
    private Duration duration;

    @NotNull(message = "Дата релиза обязательна")
    @EqualOrAfter(message = "Дата релиза не может быть раньше 28.12.1895")
    private LocalDate releaseDate;

    private Long mpaId;
    private Mpa mpa;

    @Builder.Default
    private Set<Genre> genres = new HashSet<>();

    @Builder.Default
    private Set<Long> genreIds = new HashSet<>();

    @Builder.Default
    private Set<Long> likes = new HashSet<>();

    @AssertTrue(message = "Продолжительность фильма должна быть положительным числом")
    private boolean isValidDuration() {
        return duration != null && !duration.isZero() && !duration.isNegative();
    }
}
