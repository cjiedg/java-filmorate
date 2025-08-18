package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    public static final Map<Long, Film> films = new HashMap<>();

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film create(Film film) {
        long id = generateNextId();
        film.setId(id);
        films.put(id, film);
        return films.get(id);
    }

    @Override
    public Film update(Film newFilm) {
        Long newFilmId = newFilm.getId();
        Film oldFilm = findById(newFilmId);
        String newFilmName = newFilm.getName();

        if (newFilmName != null && !newFilmName.isBlank()) {
            oldFilm.setName(newFilmName);
        }
        log.trace("Обновление полей фильм id = {}", newFilmId);
        oldFilm.setDescription(Objects.requireNonNullElse(newFilm.getDescription(), oldFilm.getDescription()));
        oldFilm.setReleaseDate(Objects.requireNonNullElse(newFilm.getReleaseDate(), oldFilm.getReleaseDate()));
        oldFilm.setDuration(Objects.requireNonNullElse(newFilm.getDuration(), oldFilm.getDuration()));
        return oldFilm;
    }

    @Override
    public Film findById(long id) {
        validateId(id);
        if (!films.containsKey(id)) {
            String msg = "Фильм с id = " + id + " не найден";
            log.warn(msg);
            throw new NotFoundException(msg);
        }
        return films.get(id);
    }

    @Override
    public void delete(long id) {
        films.remove(this.findById(id).getId());
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

    private Long generateNextId() {
        return films.keySet().stream()
                .max(Long::compareTo)
                .orElse(0L) + 1;
    }
}
