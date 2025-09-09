package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;

    @Override
    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    @Override
    public Film getFilmById(Long id) {
        validateId(id);
        return getFilmOrThrow(id);
    }

    @Override
    public Film create(Film film) {
        return filmStorage.create(film);
    }

    @Override
    public Film update(Film newFilm) {
        Long newFilmId = newFilm.getId();
        Film oldFilm = getFilmById(newFilmId);
        String newFilmName = newFilm.getName();
        log.trace("Обновление полей фильм id = {}", newFilm.getId());
        if (newFilmName != null && !newFilmName.isBlank()) {
            oldFilm.setName(newFilmName);
        }
        if (newFilm.getGenres() != null && !newFilm.getGenres().isEmpty()) {
            oldFilm.setGenres(new ArrayList<>(newFilm.getGenres()));
        }
        return filmStorage.update(newFilm, oldFilm);
    }

    @Override
    public void delete(Long id) {
        validateId(id);
        userService.delete(getFilmOrThrow(id).getId());
    }

    @Override
    public void addLike(long userId, long filmId) {
        User user = userService.getUserById(userId);
        getFilmOrThrow(filmId).getLikes().add(userId);
    }

    @Override
    public void removeLike(long userId, long filmId) {
        User user = userService.getUserById(userId);
        getFilmOrThrow(filmId).getLikes().remove(userId);
    }

    @Override
    public List<Film> getTopFilms(int count) {
        if (count <= 0) {
            String msg = "Количество фильмов должно быть положительным числом";
            log.warn(msg);
            throw new ValidationException(msg);
        }
        return filmStorage.findAll().stream()
                .sorted(Comparator.comparingInt((Film f) -> f.getLikes().size())
                        .reversed()
                )
                .limit(count)
                .collect(Collectors.toList()
                );
    }

    private void validateId(Long id) {
        if (id == null) {
            String msg = "Id должен быть указан";
            log.warn(msg);
            throw new ValidationException(msg);
        }
        if (id <= 0) {
            String msg = "Id должен быть положительным числом";
            log.warn(msg);
            throw new ValidationException(msg);
        }
    }

    private Film getFilmOrThrow(Long filmId) {
        return filmStorage.getFilmById(filmId)
                .orElseThrow(() -> {
                    String msg = "Фильм с id = " + filmId + " не найден";
                    log.warn(msg);
                    throw new NotFoundException(msg);
                });
    }
}
