package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class BaseRepository<T> {

    protected final JdbcTemplate jdbcTemplate;
    protected final RowMapper<T> rowMapper;

    protected BaseRepository(JdbcTemplate jdbcTemplate, RowMapper<T> rowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = rowMapper;
    }

    protected Optional<T> findById(String sql, Long id) {
        try {
            T entity = jdbcTemplate.queryForObject(sql, rowMapper, id);
            return Optional.ofNullable(entity);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public List<T> findByIds(Collection<Long> ids, String sqlTemplate, String idColumn) {
        if (ids == null || ids.isEmpty()) return Collections.emptyList();

        String inSql = ids.stream().map(String::valueOf).collect(Collectors.joining(","));
        String sql = sqlTemplate.replace("{ids}", inSql);
        return jdbcTemplate.query(sql, rowMapper);
    }

    protected List<T> findAll(String sql) {
        return jdbcTemplate.query(sql, rowMapper);
    }

    protected Long insert(String sql, Object... params) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    protected void update(String sql, Object... params) {
        jdbcTemplate.update(sql, params);
    }

    protected void delete(String sql, Long id) {
        jdbcTemplate.update(sql, id);
    }
}