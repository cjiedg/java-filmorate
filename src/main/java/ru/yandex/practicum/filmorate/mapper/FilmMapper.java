package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.Film.CreateFilmRequest;
import ru.yandex.practicum.filmorate.dto.Film.FilmDto;
import ru.yandex.practicum.filmorate.dto.Film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FilmMapper {

    public static Film mapToFilm(CreateFilmRequest request) {
        Film film = new Film();
        film.setName(request.getName());
        film.setDescription(request.getDescription());
        film.setReleaseDate(request.getReleaseDate());

        if (request.getDuration() != null) {
            film.setDuration(Duration.ofMinutes(request.getDuration()));
        }

        film.setMpaId(request.getMpaId());

        if (request.getGenreId() != null) {
            film.setGenreIds(new HashSet<>(request.getGenreId()));
            film.setGenres(request.getGenreId().stream()
                    .map(id -> new Genre(id, null))
                    .collect(Collectors.toSet()));
        }

        return film;
    }

    public static Film mapToFilm(UpdateFilmRequest request, Film existingFilm) {
        if (request.getName() != null) {
            existingFilm.setName(request.getName());
        }
        if (request.getDescription() != null) {
            existingFilm.setDescription(request.getDescription());
        }
        if (request.getReleaseDate() != null) {
            existingFilm.setReleaseDate(request.getReleaseDate());
        }
        if (request.getDuration() != null) {
            existingFilm.setDuration(Duration.ofMinutes(request.getDuration()));
        }
        if (request.getMpaId() != null) {
            existingFilm.setMpaId(request.getMpaId());
        }
        if (request.getGenreIds() != null) {
            existingFilm.setGenreIds(new HashSet<>(request.getGenreIds()));
            existingFilm.setGenres(request.getGenreIds().stream()
                    .map(id -> new Genre(id, null))
                    .collect(Collectors.toSet()));
        }
        return existingFilm;
    }

    public static FilmDto mapToFilmDto(Film film) {
        FilmDto dto = new FilmDto();
        dto.setId(film.getId());
        dto.setName(film.getName());
        dto.setDescription(film.getDescription());
        dto.setReleaseDate(film.getReleaseDate());
        dto.setDuration(film.getDuration() != null ? film.getDuration().toMinutes() : null);

        if (film.getMpa() != null) {
            MpaDto mpaDto = new MpaDto();
            mpaDto.setId(film.getMpa().getId());
            mpaDto.setName(film.getMpa().getName());
            dto.setMpa(mpaDto);
        } else if (film.getMpaId() != null) {
            MpaDto mpaDto = new MpaDto();
            mpaDto.setId(film.getMpaId());
            dto.setMpa(mpaDto);
        }

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            Set<GenreDto> genreDtos = film.getGenres().stream()
                    .map(genre -> {
                        GenreDto g = new GenreDto();
                        g.setId(genre.getId());
                        g.setName(genre.getName());
                        return g;
                    })
                    .collect(Collectors.toSet());
            dto.setGenres(genreDtos);
        }

        dto.setLikesCount(film.getLikes() != null ? film.getLikes().size() : 0);
        return dto;
    }
}

