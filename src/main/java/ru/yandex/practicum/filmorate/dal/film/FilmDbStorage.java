package ru.yandex.practicum.filmorate.dal.film;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@Primary
public class FilmDbStorage extends BaseRepository<Film> implements FilmStorage {

    private static final String FIND_ALL_SQL = "SELECT f.* FROM films f";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM films WHERE film_id = ?";
    private static final String INSERT_FILM_SQL = "INSERT INTO films (name, description, duration, release_date, rating_id) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_FILM_SQL = "UPDATE films SET name = ?, description = ?, duration = ?, release_date = ?, rating_id = ? WHERE film_id = ?";
    private static final String DELETE_FILM_SQL = "DELETE FROM films WHERE film_id = ?";
    private static final String INSERT_GENRE_FILM_SQL = "INSERT INTO genre_film (film_id, genre_id) VALUES (?, ?)";
    private static final String DELETE_GENRES_FOR_FILM_SQL = "DELETE FROM genre_film WHERE film_id = ?";
    private static final String GET_FILM_LIKES_SQL = "SELECT user_id FROM film_likes WHERE film_id = ?";
    private static final String INSERT_LIKE_SQL = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";
    private static final String DELETE_LIKE_SQL = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";

    private final GenreRowMapper genreRowMapper;
    private final MpaRowMapper mpaRowMapper;

    public FilmDbStorage(JdbcTemplate jdbcTemplate,
                         FilmRowMapper filmRowMapper,
                         GenreRowMapper genreRowMapper,
                         MpaRowMapper mpaRowMapper) {
        super(jdbcTemplate, filmRowMapper);
        this.genreRowMapper = genreRowMapper;
        this.mpaRowMapper = mpaRowMapper;
    }

    @Override
    public Collection<Film> findAll() {
        List<Film> films = super.findAll(FIND_ALL_SQL);
        loadGenresForFilms(films);
        loadLikesForFilms(films);
        loadMpaForFilms(films);
        return films;
    }

    @Override
    public Film create(Film film) {

        Long generatedId = super.insert(INSERT_FILM_SQL,
                film.getName(),
                film.getDescription(),
                film.getDuration() != null ? film.getDuration().toMinutes() : null,
                film.getReleaseDate() != null ? Date.valueOf(film.getReleaseDate()) : null,
                film.getMpaId());
        film.setId(generatedId);

        saveFilmGenres(film);
        return getFilmById(film.getId()).orElse(film);
    }

    @Override
    public Film update(Film newFilm, Film oldFilm) {
        super.update(UPDATE_FILM_SQL,
                newFilm.getName(),
                newFilm.getDescription(),
                newFilm.getDuration() != null ? newFilm.getDuration().toMinutes() : null,
                newFilm.getReleaseDate() != null ? Date.valueOf(newFilm.getReleaseDate()) : null,
                newFilm.getMpaId(),
                oldFilm.getId());

        updateFilmGenres(newFilm);
        return getFilmById(oldFilm.getId()).orElse(newFilm);
    }

    @Override
    public Optional<Film> getFilmById(long id) {
        return super.findById(FIND_BY_ID_SQL, id).map(film -> {

            loadFilmGenres(film);
            loadFilmLikes(film);
            loadFilmMpa(film);
            return film;
        });
    }

    @Override
    public void delete(long id) {

        jdbcTemplate.update(DELETE_GENRES_FOR_FILM_SQL, id);
        jdbcTemplate.update("DELETE FROM film_likes WHERE film_id = ?", id);
        super.delete(DELETE_FILM_SQL, id);
    }

    @Override
    public void addLike(long filmId, long userId) {
        jdbcTemplate.update(INSERT_LIKE_SQL, filmId, userId);
    }

    @Override
    public void removeLike(long filmId, long userId) {
        jdbcTemplate.update(DELETE_LIKE_SQL, filmId, userId);
    }

    @Override
    public Collection<Long> getLikesForFilm(Long id) {
        return jdbcTemplate.query(GET_FILM_LIKES_SQL,
                (rs, rowNum) -> rs.getLong("user_id"),
                id);
    }


    private void saveFilmGenres(Film film) {
        if (film.getGenreIds() == null || film.getGenreIds().isEmpty()) return;
        List<Object[]> batchArgs = film.getGenreIds().stream()
                .map(gid -> new Object[]{film.getId(), gid})
                .collect(Collectors.toList());
        jdbcTemplate.batchUpdate(INSERT_GENRE_FILM_SQL, batchArgs);
    }

    private void updateFilmGenres(Film film) {
        jdbcTemplate.update(DELETE_GENRES_FOR_FILM_SQL, film.getId());
        saveFilmGenres(film);
    }

    private void loadFilmGenres(Film film) {
        List<Genre> genres = jdbcTemplate.query(
                "SELECT g.genre_id, g.name FROM genres g JOIN genre_film gf ON g.genre_id = gf.genre_id WHERE gf.film_id = ? ORDER BY g.genre_id",
                (rs, rowNum) -> new Genre(rs.getLong("genre_id"), rs.getString("name")),
                film.getId());
        Set<Genre> genreSet = new HashSet<>(genres);
        film.setGenres(genreSet);
        film.setGenreIds(genreSet.stream().map(Genre::getId).collect(Collectors.toSet()));
    }

    private void loadGenresForFilms(List<Film> films) {
        if (films.isEmpty()) return;
        String ids = films.stream().map(f -> f.getId().toString()).collect(Collectors.joining(","));
        String sql = "SELECT gf.film_id, g.genre_id, g.name FROM genre_film gf JOIN genres g ON gf.genre_id = g.genre_id WHERE gf.film_id IN (" + ids + ")";
        Map<Long, Set<Genre>> map = new HashMap<>();
        jdbcTemplate.query(sql, rs -> {
            Long filmId = rs.getLong("film_id");
            Genre genre = new Genre(rs.getLong("genre_id"), rs.getString("name"));
            map.computeIfAbsent(filmId, k -> new HashSet<>()).add(genre);
        });
        films.forEach(f -> {
            Set<Genre> gs = map.getOrDefault(f.getId(), new HashSet<>());
            f.setGenres(gs);
            f.setGenreIds(gs.stream().map(Genre::getId).collect(Collectors.toSet()));
        });
    }

    private void loadFilmLikes(Film film) {
        Collection<Long> likes = getLikesForFilm(film.getId());
        film.getLikes().clear();
        film.getLikes().addAll(likes);
    }

    private void loadLikesForFilms(List<Film> films) {
        if (films.isEmpty()) return;
        String ids = films.stream().map(f -> f.getId().toString()).collect(Collectors.joining(","));
        String sql = "SELECT film_id, user_id FROM film_likes WHERE film_id IN (" + ids + ")";
        Map<Long, Set<Long>> map = new HashMap<>();
        jdbcTemplate.query(sql, rs -> {
            Long filmId = rs.getLong("film_id");
            Long userId = rs.getLong("user_id");
            map.computeIfAbsent(filmId, k -> new HashSet<>()).add(userId);
        });
        films.forEach(f -> {
            Set<Long> likes = map.getOrDefault(f.getId(), new HashSet<>());
            f.getLikes().clear();
            f.getLikes().addAll(likes);
        });
    }

    private void loadFilmMpa(Film film) {
        if (film.getMpaId() == null) return;
        String sql = "SELECT r.rating_id, r.name FROM rating r WHERE r.rating_id = ?";
        Mpa mpa = jdbcTemplate.queryForObject(sql, mpaRowMapper, film.getMpaId());
        film.setMpa(mpa);
    }

    private void loadMpaForFilms(List<Film> films) {

        Map<Long, List<Film>> map = films.stream()
                .filter(f -> f.getMpaId() != null)
                .collect(Collectors.groupingBy(Film::getMpaId));
        if (map.isEmpty()) return;
        String mpaIds = map.keySet().stream().map(String::valueOf).collect(Collectors.joining(","));
        String sql = "SELECT r.* FROM rating r WHERE r.rating_id IN (" + mpaIds + ")";
        jdbcTemplate.query(sql, rs -> {
            Mpa mpa = mpaRowMapper.mapRow(rs, 0);
            films.stream().filter(f -> f.getMpaId() != null && f.getMpaId().equals(mpa.getId()))
                    .forEach(f -> f.setMpa(mpa));
        });
    }
}
