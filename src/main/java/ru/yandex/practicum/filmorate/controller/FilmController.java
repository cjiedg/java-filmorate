package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    public static final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Запрос на получение списка всех фильмов");
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody @Valid Film film) {
        log.info("Запрос на создание фильма {}", film);
        film.setId(generateNextId());
        films.put(film.getId(), film);
        log.debug("Создан фильм с id = {}", film.getId());
        return film;
    }

    @PutMapping
    public Film update(@RequestBody @Valid Film newFilm) {
        log.info("Запрос на обновление фильма {}", newFilm);
        Long newFilmId = newFilm.getId();
        if (newFilmId == null) {
            String msg = "Id должен быть указан";
            log.warn(msg);
            throw new ValidationException(msg);
        }
        if (films.containsKey(newFilmId)) {
            Film oldFilm = films.get(newFilmId);

            if (!(newFilm.getName() == null) && !newFilm.getName().isBlank()) {
                oldFilm.setName(newFilm.getName());
            }
            log.trace("Обновление полей фильм id = {}", newFilmId);
            oldFilm.setDescription(Objects.requireNonNullElse(newFilm.getDescription(), oldFilm.getDescription()));
            oldFilm.setReleaseDate(Objects.requireNonNullElse(newFilm.getReleaseDate(), oldFilm.getReleaseDate()));
            oldFilm.setDuration(Objects.requireNonNullElse(newFilm.getDuration(), oldFilm.getDuration()));
            log.debug("Фильм с id = {} успешно обновлён: {}", newFilmId, newFilm);
            return oldFilm;
        }
        String msg = "Фильм с id = " + newFilmId + " не найден";
        log.warn(msg);
        throw new NotFoundException(msg);
    }

    private Long generateNextId() {
        return films.keySet().stream()
                .max(Long::compareTo)
                .orElse(0L) + 1;
    }
}
