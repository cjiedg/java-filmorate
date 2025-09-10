package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.Film.CreateFilmRequest;
import ru.yandex.practicum.filmorate.dto.Film.FilmDto;
import ru.yandex.practicum.filmorate.dto.Film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public List<FilmDto> findAll() {
        log.info("Запрос на получение списка всех фильмов");
        List<FilmDto> allFilms = filmService.findAll();
        log.debug("Найдено фильмов: {}", allFilms.size());
        return allFilms;
    }

    @GetMapping("/{id}")
    public FilmDto getFilmById(@PathVariable Long id) {
        log.info("Запрос на получение фильма с id = {}", id);
        FilmDto foundFilm = filmService.getFilmById(id);
        log.debug("Найден фильм с id = {}", id);
        return foundFilm;
    }

    @PostMapping
    public FilmDto create(@RequestBody @Valid CreateFilmRequest filmDto) {
        log.info("Запрос на создание фильма {}", filmDto);
        FilmDto createdFilm = filmService.create(filmDto);
        log.debug("Создан фильм с id = {}", createdFilm.getId());
        return createdFilm;
    }

    @PutMapping
    public FilmDto update(@RequestBody @Valid UpdateFilmRequest filmDto) {
        log.info("Запрос на обновление фильма {}", filmDto);
        FilmDto updatedFilm = filmService.update(filmDto);
        log.debug("Фильм с id = {} успешно обновлён", updatedFilm.getId());
        return updatedFilm;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long id) {
        log.info("Запрос на удаление фильма с id = {}", id);
        filmService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/popular")
    public List<FilmDto> getTopFilms(@RequestParam(required = false, defaultValue = "10") int count) {
        log.info("Запрос на получение списка популярных фильмов. Количество: {}", count);
        List<FilmDto> topFilms = filmService.getTopFilms(count);
        log.debug("Найдено популярных фильмов: {}", topFilms.size());
        return topFilms;
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<Void> addLike(@PathVariable("id") Long filmId, @PathVariable Long userId) {
        log.info("Добавление лайка: фильм ID {}, пользователь ID {}", filmId, userId);
        filmService.addLike(filmId, userId);
        log.debug("Добавлен лайк: фильм ID {}, пользователь ID {}", filmId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<Void> removeLike(@PathVariable("id") Long filmId, @PathVariable Long userId) {
        log.info("Удаление лайка: фильм ID {}, пользователь ID {}", filmId, userId);
        filmService.removeLike(filmId, userId);
        log.debug("Удален лайк: фильм ID {}, пользователь ID {}", filmId, userId);
        return ResponseEntity.noContent().build();
    }
}