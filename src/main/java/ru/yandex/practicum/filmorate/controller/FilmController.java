package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmStorage filmStorage;
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmStorage filmStorage, FilmService filmService) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Запрос на получение списка всех фильмов");
        Collection<Film> allFilms = filmStorage.findAll();
        log.debug("Найдено фильмов: {}", allFilms.size());
        return allFilms;
    }

    @GetMapping("/{id}")
    public Film findFilm(@PathVariable Long id) {
        log.info("Запрос на получение фильма с id = {}", id);
        Film foundFilm = filmStorage.findById(id);
        log.debug("Найден фильм с id = {}", id);
        return foundFilm;
    }

    @PostMapping
    public Film create(@RequestBody @Valid Film film) {
        log.info("Запрос на создание фильма {}", film);
        Film createdFilm = filmStorage.create(film);
        log.debug("Создан фильм с id = {}", film.getId());
        return createdFilm;
    }

    @PutMapping
    public Film update(@RequestBody @Valid Film newFilm) {
        log.info("Запрос на обновление фильма {}", newFilm);
        Film updatedFilm = filmStorage.update(newFilm);
        log.debug("Фильм с id = {} успешно обновлён: {}", updatedFilm.getId(), updatedFilm);
        return updatedFilm;
    }

    @GetMapping("/popular")
    public Collection<Film> getTopFilms(@RequestParam(required = false, defaultValue = "10") int count) {
        log.info("Запрос на получение списка популярных фильмов. Количество: {}", count);
        Collection<Film> topFilms = filmService.getTopFilms(count);
        log.debug("Найдено популярных фильмов: {}", topFilms.size());
        return topFilms;
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(
            @PathVariable("id") Long filmId,
            @PathVariable Long userId) {
        log.info("Добавление лайка: фильм ID {}, пользователь ID {}", filmId, userId);
        filmService.addLike(userId, filmId);
        log.debug("Добавлен лайк: фильм ID {}, пользователь ID {}", filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(
            @PathVariable("id") Long filmId,
            @PathVariable Long userId) {
        log.info("Удаление лайка: фильм ID {}, пользователь ID {}", filmId, userId);
        filmService.removeLike(userId, filmId);
        log.debug("Удален лайк: фильм ID {}, пользователь ID {}", filmId, userId);
    }
}
