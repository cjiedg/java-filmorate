package ru.yandex.practicum.filmorate.service.genre;

import ru.yandex.practicum.filmorate.dto.GenreDto;

import java.util.List;

public interface GenreService {
    List<GenreDto> getAllGenres();

    GenreDto getGenreById(long id);
}
