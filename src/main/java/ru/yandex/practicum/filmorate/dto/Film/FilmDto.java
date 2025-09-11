package ru.yandex.practicum.filmorate.dto.Film;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.dto.MpaDto;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class FilmDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Long duration;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private MpaDto mpa;
    private Set<GenreDto> genres = new HashSet<>();
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer likesCount;
}