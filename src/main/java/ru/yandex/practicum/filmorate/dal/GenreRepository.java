package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

@Repository
public class GenreRepository extends BaseRepository<Genre> {

    private static final String FIND_ALL_SQL = "SELECT * FROM genres ORDER BY genre_id";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM genres WHERE genre_id = ?";

    public GenreRepository(JdbcTemplate jdbcTemplate, RowMapper<Genre> genreRowMapper) {
        super(jdbcTemplate, genreRowMapper);
    }

    public List<Genre> findAll() {
        return super.findAll(FIND_ALL_SQL);
    }

    public Optional<Genre> findById(long id) {
        return super.findById(FIND_BY_ID_SQL, id);
    }
}