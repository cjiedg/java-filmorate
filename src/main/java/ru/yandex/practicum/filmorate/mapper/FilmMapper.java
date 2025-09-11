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
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
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

        if (request.getMpa() != null) {
            film.setMpaId(request.getMpa().getId());
        }

        if (request.getGenres() != null && !request.getGenres().isEmpty()) {
            Set<Long> genreIds = request.getGenres().stream()
                    .map(Genre::getId)
                    .collect(Collectors.toSet());
            film.setGenreIds(genreIds);

            Set<Genre> genres = request.getGenres().stream()
                    .map(g -> new Genre(g.getId(), g.getName()))
                    .collect(Collectors.toSet());
            film.setGenres(genres);
        }
        return film;
    }

    public static Film mapToFilm(UpdateFilmRequest request, Film existingFilm) {
        if (request.getName() != null) existingFilm.setName(request.getName());
        if (request.getDescription() != null) existingFilm.setDescription(request.getDescription());
        if (request.getReleaseDate() != null) existingFilm.setReleaseDate(request.getReleaseDate());
        if (request.getDuration() != null) existingFilm.setDuration(Duration.ofMinutes(request.getDuration()));

        if (request.getMpa() != null) {
            existingFilm.setMpaId(request.getMpa().getId());
        }

        if (request.getGenres() != null && !request.getGenres().isEmpty()) {
            Set<Long> genreIds = request.getGenres().stream()
                    .map(Genre::getId)
                    .collect(Collectors.toSet());
            existingFilm.setGenreIds(genreIds);

            Set<Genre> genres = request.getGenres().stream()
                    .map(g -> new Genre(g.getId(), g.getName()))
                    .collect(Collectors.toSet());
            existingFilm.setGenres(genres);
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
            List<GenreDto> genreDtos = film.getGenres().stream()
                    .sorted(Comparator.comparingLong(Genre::getId))
                    .map(genre -> {
                        GenreDto g = new GenreDto();
                        g.setId(genre.getId());
                        g.setName(genre.getName());
                        return g;
                    })
                    .collect(Collectors.toList());
            dto.setGenres(new LinkedHashSet<>(genreDtos));
        }

        dto.setLikesCount(film.getLikes() != null ? film.getLikes().size() : 0);
        return dto;
    }
}


