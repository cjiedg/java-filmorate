package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

@Repository
public class MpaRepository extends BaseRepository<Mpa> {

    private static final String FIND_ALL_SQL = "SELECT * FROM rating ORDER BY rating_id";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM rating WHERE rating_id = ?";

    public MpaRepository(JdbcTemplate jdbcTemplate, RowMapper<Mpa> mpaRatingRowMapper) {
        super(jdbcTemplate, mpaRatingRowMapper);
    }

    public List<Mpa> findAll() {
        return super.findAll(FIND_ALL_SQL);
    }

    public Optional<Mpa> findById(long id) {
        return super.findById(FIND_BY_ID_SQL, id);
    }
}