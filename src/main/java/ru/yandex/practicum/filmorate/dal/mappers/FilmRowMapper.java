package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;

@Component
public class FilmRowMapper implements RowMapper<Film> {

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getLong("film_id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));

        Date sqlDate = rs.getDate("release_date");
        if (sqlDate != null) {
            film.setReleaseDate(sqlDate.toLocalDate());
        }

        Long durationMinutes = rs.getObject("duration", Long.class);
        if (durationMinutes != null) {
            film.setDuration(Duration.ofMinutes(durationMinutes));
        } else {
            film.setDuration(Duration.ZERO);
        }

        Long mpaId = rs.getObject("rating_id", Long.class);
        film.setMpaId(mpaId);


        return film;
    }
}
