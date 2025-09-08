package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;
import java.util.Optional;

@Repository
public class MpaRatingRepository extends BaseRepository<MpaRating> {

    private static final String FIND_ALL_SQL = "SELECT * FROM rating ORDER BY rating_id";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM rating WHERE rating_id = ?";

    public MpaRatingRepository(JdbcTemplate jdbcTemplate, RowMapper<MpaRating> mpaRatingRowMapper) {
        super(jdbcTemplate, mpaRatingRowMapper);
    }

    public List<MpaRating> findAll() {
        return super.findAll(FIND_ALL_SQL);
    }

    public Optional<MpaRating> findById(long id) {
        return super.findById(FIND_BY_ID_SQL, id);
    }
}