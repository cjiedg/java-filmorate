package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

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
    public Film update(Film newFilm, Film oldFilm) {
        oldFilm.setDescription(Objects.requireNonNullElse(newFilm.getDescription(), oldFilm.getDescription()));
        oldFilm.setReleaseDate(Objects.requireNonNullElse(newFilm.getReleaseDate(), oldFilm.getReleaseDate()));
        oldFilm.setDuration(Objects.requireNonNullElse(newFilm.getDuration(), oldFilm.getDuration()));
        oldFilm.setRating(Objects.requireNonNullElse(newFilm.getRating(), oldFilm.getRating()));
        return oldFilm;
    }

    @Override
    public Optional<Film> getFilmById(long id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public void delete(long id) {
        films.remove(id);
    }

    private Long generateNextId() {
        return films.keySet().stream()
                .max(Long::compareTo)
                .orElse(0L) + 1;
    }
}
