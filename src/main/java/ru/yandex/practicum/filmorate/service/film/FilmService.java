package ru.yandex.practicum.filmorate.service.film;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmService {
    void addLike(long userId, long filmId);

    void removeLike(long userId, long filmId);

    List<Film> getTopFilms(int count) throws ValidationException;

    Collection<Film> findAll();

    Film getFilmById(Long id);

    Film create(Film film);

    Film update(Film newFilm);

    void delete(Long id);
}
