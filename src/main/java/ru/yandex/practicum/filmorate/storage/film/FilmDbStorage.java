package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Component
@Primary
public class FilmDbStorage extends BaseRepository<Film> implements FilmStorage {

    // SQL запросы для фильмов
    private static final String FIND_ALL_SQL = "SELECT f.*, r.rating_id as mpa_id, r.name as rating_name FROM films f LEFT JOIN rating r ON f.rating_id = r.rating_id";
    private static final String FIND_BY_ID_SQL = "SELECT f.*, r.rating_id as mpa_id, r.name as rating_name FROM films f LEFT JOIN rating r ON f.rating_id = r.rating_id WHERE f.film_id = ?";
    private static final String INSERT_FILM_SQL = "INSERT INTO films (name, description, duration, release_date, rating_id) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_FILM_SQL = "UPDATE films SET name = ?, description = ?, duration = ?, release_date = ?, rating_id = ? WHERE film_id = ?";
    private static final String DELETE_FILM_SQL = "DELETE FROM films WHERE film_id = ?";

    // SQL запросы для жанров
    private static final String GET_FILM_GENRE_IDS_SQL = "SELECT genre_id FROM genre_film WHERE film_id = ? ORDER BY genre_id";
    private static final String GET_GENRE_IDS_FOR_FILMS_SQL = "SELECT film_id, genre_id FROM genre_film WHERE film_id IN (%s) ORDER BY film_id, genre_id";
    private static final String INSERT_GENRE_FILM_SQL = "INSERT INTO genre_film (film_id, genre_id) VALUES (?, ?)";
    private static final String DELETE_GENRES_FOR_FILM_SQL = "DELETE FROM genre_film WHERE film_id = ?";

    // SQL запросы для лайков
    private static final String GET_FILM_LIKES_SQL = "SELECT user_id FROM film_likes WHERE film_id = ?";
    private static final String GET_LIKES_FOR_FILMS_SQL = "SELECT film_id, user_id FROM film_likes WHERE film_id IN (%s)";
    private static final String INSERT_LIKE_SQL = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";
    private static final String DELETE_LIKES_FOR_FILM_SQL = "DELETE FROM film_likes WHERE film_id = ?";
    private static final String DELETE_LIKE_SQL = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";

    // SQL запрос для популярных фильмов
    private static final String GET_POPULAR_FILMS_SQL = "SELECT f.*, r.rating_id as mpa_id, r.name as rating_name, COUNT(fl.user_id) as likes_count " +
            "FROM films f LEFT JOIN rating r ON f.rating_id = r.rating_id " +
            "LEFT JOIN film_likes fl ON f.film_id = fl.film_id " +
            "GROUP BY f.film_id, f.name, f.description, f.duration, f.release_date, f.rating_id, r.rating_id, r.name " +
            "ORDER BY COUNT(fl.user_id) DESC LIMIT ?";

    public FilmDbStorage(JdbcTemplate jdbcTemplate, RowMapper<Film> filmRowMapper) {
        super(jdbcTemplate, filmRowMapper);
    }

    @Override
    public Collection<Film> findAll() {
        List<Film> films = super.findAll(FIND_ALL_SQL);
        loadGenreIdsForFilms(films);
        loadLikesForFilms(films);
        return films;
    }

    @Override
    public Film create(Film film) {
        Long filmId = super.insert(INSERT_FILM_SQL,
                film.getName(),
                film.getDescription(),
                film.getDuration().toMinutes(),
                film.getReleaseDate(),
                film.getRating());

        film.setId(filmId);
        saveFilmGenres(film);

        return getFilmById(filmId).orElse(film);
    }

    @Override
    public Film update(Film newFilm, Film oldFilm) {
        super.update(UPDATE_FILM_SQL,
                newFilm.getName(),
                newFilm.getDescription(),
                newFilm.getDuration().toMinutes(),
                newFilm.getReleaseDate(),
                newFilm.getRating(),
                oldFilm.getId());

        updateFilmGenres(newFilm);
        return getFilmById(oldFilm.getId()).orElse(newFilm);
    }

    @Override
    public Optional<Film> getFilmById(long id) {
        Optional<Film> filmOptional = super.findById(FIND_BY_ID_SQL, id);
        filmOptional.ifPresent(film -> {
            loadFilmGenreIds(film);
            loadFilmLikes(film);
        });
        return filmOptional;
    }

    @Override
    public void delete(long id) {
        jdbcTemplate.update(DELETE_LIKES_FOR_FILM_SQL, id);
        jdbcTemplate.update(DELETE_GENRES_FOR_FILM_SQL, id);
        super.delete(DELETE_FILM_SQL, id);
    }

    private void updateFilmGenres(Film film) {
        jdbcTemplate.update(DELETE_GENRES_FOR_FILM_SQL, film.getId());
        saveFilmGenres(film);
    }

    private void loadFilmLikes(Film film) {
        List<Long> likes = jdbcTemplate.query(GET_FILM_LIKES_SQL,
                (rs, rowNum) -> rs.getLong("user_id"),
                film.getId());

        film.getLikes().clear();
        film.getLikes().addAll(likes);
    }

    private void loadLikesForFilms(List<Film> films) {
        if (films.isEmpty()) return;

        String filmIds = films.stream()
                .map(f -> String.valueOf(f.getId()))
                .collect(Collectors.joining(","));

        String sql = String.format(GET_LIKES_FOR_FILMS_SQL, filmIds);
        Map<Long, List<Long>> filmLikesMap = new HashMap<>();

        jdbcTemplate.query(sql, rs -> {
            Long filmId = rs.getLong("film_id");
            Long userId = rs.getLong("user_id");
            filmLikesMap.computeIfAbsent(filmId, k -> new ArrayList<>()).add(userId);
        });

        films.forEach(film -> {
            List<Long> likes = filmLikesMap.getOrDefault(film.getId(), new ArrayList<>());
            film.getLikes().clear();
            film.getLikes().addAll(likes);
        });
    }

    public void addLike(long filmId, long userId) {
        jdbcTemplate.update(INSERT_LIKE_SQL, filmId, userId);
    }

    public void removeLike(long filmId, long userId) {
        jdbcTemplate.update(DELETE_LIKE_SQL, filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        List<Film> films = jdbcTemplate.query(GET_POPULAR_FILMS_SQL, this.rowMapper, count);
        loadGenreIdsForFilms(films);
        loadLikesForFilms(films);
        return films;
    }

    private void loadFilmGenreIds(Film film) {
        List<Long> genreIds = jdbcTemplate.query(GET_FILM_GENRE_IDS_SQL,
                (rs, rowNum) -> rs.getLong("genre_id"),
                film.getId());
        film.setGenres(genreIds);
    }

    private void loadGenreIdsForFilms(List<Film> films) {
        if (films.isEmpty()) return;

        String filmIds = films.stream()
                .map(f -> String.valueOf(f.getId()))
                .collect(Collectors.joining(","));

        String sql = String.format(GET_GENRE_IDS_FOR_FILMS_SQL, filmIds);
        Map<Long, List<Long>> filmGenresMap = new HashMap<>();

        jdbcTemplate.query(sql, rs -> {
            Long filmId = rs.getLong("film_id");
            Long genreId = rs.getLong("genre_id");
            filmGenresMap.computeIfAbsent(filmId, k -> new ArrayList<>()).add(genreId);
        });

        films.forEach(film -> {
            List<Long> genreIds = filmGenresMap.getOrDefault(film.getId(), new ArrayList<>());
            film.setGenres(genreIds);
        });
    }

    private void saveFilmGenres(Film film) {
        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            return;
        }

        List<Object[]> batchArgs = film.getGenres().stream()
                .map(genreId -> new Object[]{film.getId(), genreId})
                .collect(Collectors.toList());

        jdbcTemplate.batchUpdate(INSERT_GENRE_FILM_SQL, batchArgs);
    }
}