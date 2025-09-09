package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.Film.CreateFilmRequest;
import ru.yandex.practicum.filmorate.dto.Film.FilmDto;
import ru.yandex.practicum.filmorate.dto.Film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.time.Duration;
import java.util.ArrayList;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FilmMapper {

    public static Film mapToFilm(CreateFilmRequest request) {
        Film film = new Film();
        film.setName(request.getName());
        film.setDescription(request.getDescription());
        film.setReleaseDate(request.getReleaseDate());
        film.setDuration(Duration.ofMinutes(request.getDuration()));
        MpaRating mpa = new MpaRating();
        mpa.setId(request.getMpaId());
        film.setRating(mpa.getId());

        if (request.getGenreIds() != null) {
            film.setGenres(request.getGenreIds());
        }
        return film;
    }

    public static FilmDto mapToFilmDto(Film film) {
        FilmDto dto = new FilmDto();
        dto.setId(film.getId());
        dto.setName(film.getName());
        dto.setDescription(film.getDescription());
        dto.setReleaseDate(film.getReleaseDate());
        dto.setDuration(film.getDuration() != null ? film.getDuration().toMinutes() : null);
        if (film.getRating() != null) {
            dto.setMpaId(film.getRating());
        }
        if (film.getGenres() != null) {
            dto.setGenreIds(film.getGenres());
        } else {
            dto.setGenreIds(new ArrayList<>());
        }
        dto.setLikesCount(film.getLikes() != null ? film.getLikes().size() : 0);
        return dto;
    }

    public static Film updateFilmFields(Film film, UpdateFilmRequest request) {
        if (request.getName() != null) {
            film.setName(request.getName());
        }
        if (request.getDescription() != null) {
            film.setDescription(request.getDescription());
        }
        if (request.getReleaseDate() != null) {
            film.setReleaseDate(request.getReleaseDate());
        }
        if (request.getDuration() != null) {
            film.setDuration(Duration.ofMinutes(request.getDuration()));
        }
        if (request.getMpaId() != null) {
            film.setRating(request.getMpaId());
        }
        if (request.getGenreIds() != null) {
            film.setGenres(request.getGenreIds());
        }
        return film;
    }
}