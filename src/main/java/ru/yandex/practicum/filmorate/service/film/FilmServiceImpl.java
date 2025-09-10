package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.film.FilmStorage;
import ru.yandex.practicum.filmorate.dto.Film.CreateFilmRequest;
import ru.yandex.practicum.filmorate.dto.Film.FilmDto;
import ru.yandex.practicum.filmorate.dto.Film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.user.UserService;

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
    public List<FilmDto> findAll() {
        return filmStorage.findAll().stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    @Override
    public FilmDto getFilmById(Long id) {
        validateId(id);
        Film film = filmStorage.getFilmById(id).orElseThrow(() -> new NotFoundException("Фильм не найден"));
        return FilmMapper.mapToFilmDto(film);
    }


    @Override
    public FilmDto create(CreateFilmRequest request) {
        Film film = FilmMapper.mapToFilm(request);
        Film saved = filmStorage.create(film);
        Film full = filmStorage.getFilmById(saved.getId()).orElse(saved);
        return FilmMapper.mapToFilmDto(full);
    }

    @Override
    public FilmDto update(UpdateFilmRequest request) {
        if (request.getId() == null) throw new ValidationException("Id обязателен для обновления");
        Film existing = filmStorage.getFilmById(request.getId())
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));
        Film toUpdate = FilmMapper.mapToFilm(request, existing);
        Film saved = filmStorage.update(toUpdate, existing);
        Film full = filmStorage.getFilmById(saved.getId()).orElse(saved);
        return FilmMapper.mapToFilmDto(full);
    }

    @Override
    public void delete(Long id) {
        validateId(id);
        filmStorage.delete(id);
    }

    @Override
    public void addLike(Long filmId, Long userId) {

        userService.getUserById(userId);
        filmStorage.getFilmById(filmId).orElseThrow(() -> new NotFoundException("Фильм не найден"));
        filmStorage.addLike(filmId, userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        userService.getUserById(userId);
        filmStorage.getFilmById(filmId).orElseThrow(() -> new NotFoundException("Фильм не найден"));
        filmStorage.removeLike(filmId, userId);
    }

    @Override
    public List<FilmDto> getTopFilms(int count) {
        if (count <= 0) throw new ValidationException("Количество фильмов должно быть положительным числом");
        return filmStorage.findAll().stream()
                .sorted(Comparator.comparingInt((Film f) -> f.getLikes() != null ? f.getLikes().size() : 0).reversed())
                .limit(count)
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) throw new ValidationException("Id должен быть положительным числом");
    }
}
