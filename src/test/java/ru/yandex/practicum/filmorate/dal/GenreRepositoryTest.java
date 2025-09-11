package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({GenreRepository.class, GenreRowMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GenreRepositoryTest {

    private final GenreRepository genreRepository;
    private final JdbcTemplate jdbcTemplate;

    @BeforeAll
    static void createTable(@Autowired JdbcTemplate jdbcTemplate) {
        jdbcTemplate.execute("""
        CREATE TABLE IF NOT EXISTS genres (
            genre_id BIGINT AUTO_INCREMENT PRIMARY KEY,
            name VARCHAR(255) NOT NULL
        )
    """);
    }


    @BeforeEach
    void setupGenres() {
        
        jdbcTemplate.update("DELETE FROM genres");

        
        jdbcTemplate.update("INSERT INTO genres (genre_id, name) VALUES (?, ?)", 1, "Комедия");
        jdbcTemplate.update("INSERT INTO genres (genre_id, name) VALUES (?, ?)", 2, "Драма");
        jdbcTemplate.update("INSERT INTO genres (genre_id, name) VALUES (?, ?)", 3, "Триллер");
        jdbcTemplate.update("INSERT INTO genres (genre_id, name) VALUES (?, ?)", 4, "Фантастика");
    }

    @Test
    void testFindAll() {
        List<Genre> genres = genreRepository.findAll();

        assertThat(genres)
                .isNotNull()
                .isNotEmpty()
                .allSatisfy(genre -> {
                    assertThat(genre.getId()).isPositive();
                    assertThat(genre.getName()).isNotBlank();
                })
                .isSortedAccordingTo(Comparator.comparing(Genre::getId));

        assertThat(genres)
                .extracting(Genre::getName)
                .doesNotHaveDuplicates();
    }

    @Test
    void testFindById_WhenGenreExists() {
        Genre firstGenre = genreRepository.findAll().get(0);
        Optional<Genre> genreOptional = genreRepository.findById(firstGenre.getId());

        assertThat(genreOptional)
                .isPresent()
                .hasValueSatisfying(genre -> {
                    assertThat(genre.getId()).isEqualTo(firstGenre.getId());
                    assertThat(genre.getName()).isEqualTo(firstGenre.getName());
                });
    }

    @Test
    void testFindById_WhenGenreNotExists() {
        Optional<Genre> genreOptional = genreRepository.findById(999L);
        assertThat(genreOptional).isNotPresent();
    }

    @Test
    void testGenreDataIntegrity() {
        List<Genre> genres = genreRepository.findAll();

        assertThat(genres)
                .isNotEmpty()
                .allSatisfy(genre -> {
                    assertThat(genre.getId()).isNotNull();
                    assertThat(genre.getName()).isNotBlank();
                });
    }

    @Test
    void testFindAllConsistency() {
        List<Genre> firstCall = genreRepository.findAll();
        List<Genre> secondCall = genreRepository.findAll();

        assertThat(firstCall)
                .hasSameSizeAs(secondCall)
                .containsExactlyElementsOf(secondCall);
    }
}
