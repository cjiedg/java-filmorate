package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dal.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmDbStorage.class, FilmRowMapper.class, GenreRowMapper.class, MpaRowMapper.class})
class FilmDbStorageTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private FilmDbStorage filmDbStorage;
    private GenreRepository genreRepository;
    private MpaRowMapper mpaRowMapper;

    @BeforeEach
    void setup() {
        
        jdbcTemplate.update("DELETE FROM film_likes");
        jdbcTemplate.update("DELETE FROM genre_film");
        jdbcTemplate.update("DELETE FROM films");
        jdbcTemplate.update("DELETE FROM rating");

        
        jdbcTemplate.update("INSERT INTO rating (rating_id, name) VALUES (?, ?)", 1, "G");
        jdbcTemplate.update("INSERT INTO rating (rating_id, name) VALUES (?, ?)", 2, "PG");
        jdbcTemplate.update("INSERT INTO rating (rating_id, name) VALUES (?, ?)", 3, "PG-13");
        jdbcTemplate.update("INSERT INTO rating (rating_id, name) VALUES (?, ?)", 4, "R");

        
        jdbcTemplate.update("INSERT INTO users (user_id, login, name, email, birthday) VALUES (?, ?, ?, ?, ?)",
                1, "testuser", "Тестовый Юзер", "test@example.com", LocalDate.of(1990,1,1));

        filmDbStorage = new FilmDbStorage(jdbcTemplate, new FilmRowMapper(),new GenreRowMapper(), new MpaRowMapper());
    }


    private Film createSampleFilm() {
        Mpa mpa = new Mpa();
        Film film = new Film();
        film.setName("Интерстеллар");
        film.setDescription("Фантастический эпос о путешествии к червоточинам");
        film.setDuration(Duration.ofMinutes(169));
        film.setReleaseDate(LocalDate.of(2014, 11, 6));
        mpa.setId(1L);
        film.setMpa(mpa);
        return film;
    }

    @Test
    void testCreateAndGetFilmById() {
        Film created = filmDbStorage.create(createSampleFilm());
        Optional<Film> retrieved = filmDbStorage.getFilmById(created.getId());

        assertThat(retrieved).isPresent()
                .get()
                .isEqualToComparingFieldByField(created);
    }

    @Test
    void testUpdateFilm() {
        Film created = filmDbStorage.create(createSampleFilm());
        created.setName("Интерстеллар 2");
        created.setDescription("Продолжение фантастического эпоса");
        filmDbStorage.update(created, created);

        Optional<Film> retrieved = filmDbStorage.getFilmById(created.getId());
        assertThat(retrieved).isPresent()
                .get()
                .isEqualToComparingFieldByField(created);
    }

    @Test
    void testDeleteFilm() {
        Film created = filmDbStorage.create(createSampleFilm());
        filmDbStorage.delete(created.getId());

        Optional<Film> retrieved = filmDbStorage.getFilmById(created.getId());
        assertThat(retrieved).isEmpty();
    }

    @Test
    void testAddAndRemoveLike() {
        Film created = filmDbStorage.create(createSampleFilm());

        
        filmDbStorage.addLike(created.getId(), 1L);
        Optional<Film> retrievedAfterLike = filmDbStorage.getFilmById(created.getId());
        assertThat(retrievedAfterLike).isPresent();
        assertThat(retrievedAfterLike.get().getLikes()).contains(1L);

        
        filmDbStorage.removeLike(created.getId(), 1L);
        Optional<Film> retrievedAfterRemove = filmDbStorage.getFilmById(created.getId());
        assertThat(retrievedAfterRemove).isPresent();
        assertThat(retrievedAfterRemove.get().getLikes()).isEmpty();
    }

    @Test
    void testFindAllFilms() {
        Film film1 = filmDbStorage.create(createSampleFilm());
        Film film2 = filmDbStorage.create(createSampleFilm());

        Collection<Film> films = filmDbStorage.findAll();
        assertThat(films).hasSize(2)
                .extracting("id")
                .containsExactlyInAnyOrder(film1.getId(), film2.getId());
    }
}
