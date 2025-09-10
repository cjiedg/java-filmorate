package ru.yandex.practicum.filmorate.dal.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;


public interface FilmStorage {
    Collection<Film> findAll();

    Optional<Film> getFilmById(long id);

    Film create(Film film);

    Film update(Film newFilm, Film oldFilm);

    void delete(long id);

    void addLike(long filmId, long userId);

    void removeLike(long filmId, long userId);

    Collection<Long> getLikesForFilm(Long id);
}
