package ru.yandex.practicum.filmorate.service.film;

import ru.yandex.practicum.filmorate.dto.Film.CreateFilmRequest;
import ru.yandex.practicum.filmorate.dto.Film.FilmDto;
import ru.yandex.practicum.filmorate.dto.Film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.util.List;


public interface FilmService {
    void addLike(Long filmId, Long userId);

    void removeLike(Long filmId, Long userId);

    List<FilmDto> getTopFilms(int count) throws ValidationException;

    List<FilmDto> findAll();

    FilmDto getFilmById(Long id);

    FilmDto create(CreateFilmRequest request);

    FilmDto update(UpdateFilmRequest request);

    void delete(Long id);
}

