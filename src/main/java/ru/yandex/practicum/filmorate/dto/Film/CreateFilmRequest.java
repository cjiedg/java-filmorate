package ru.yandex.practicum.filmorate.dto.Film;

import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class CreateFilmRequest {
    private String name;

    private String description;

    private LocalDate releaseDate;

    private Long duration;

    private Long mpaId;
    private List<Long> genreIds = new ArrayList<>();
}