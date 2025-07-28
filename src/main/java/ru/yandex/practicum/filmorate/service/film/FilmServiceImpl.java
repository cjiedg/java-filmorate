package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmServiceImpl implements FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmServiceImpl(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    @Override
    public void addLike(long userId, long filmId) {
        User user = userStorage.findById(userId);
        filmStorage.findById(filmId).getLikes().add(userId);
    }

    @Override
    public void removeLike(long userId, long filmId) {
        User user = userStorage.findById(userId);
        filmStorage.findById(filmId).getLikes().remove(userId);
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
}
