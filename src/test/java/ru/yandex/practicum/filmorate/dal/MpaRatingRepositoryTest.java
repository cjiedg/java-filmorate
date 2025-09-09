package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.dal.mappers.MpaRatingRowMapper;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({MpaRatingRepository.class, MpaRatingRowMapper.class})
class MpaRatingRepositoryTest {

    private final MpaRatingRepository mpaRatingRepository;
    private final JdbcTemplate jdbcTemplate;

    
    private final List<String> validRatingNames = List.of("G", "PG", "PG-13", "R", "NC-17");

    @BeforeEach
    void setupRatings() {
        jdbcTemplate.update("DELETE FROM rating");
        jdbcTemplate.update("INSERT INTO rating (rating_id, name) VALUES (?, ?)", 1, "G");
        jdbcTemplate.update("INSERT INTO rating (rating_id, name) VALUES (?, ?)", 2, "PG");
        jdbcTemplate.update("INSERT INTO rating (rating_id, name) VALUES (?, ?)", 3, "PG-13");
        jdbcTemplate.update("INSERT INTO rating (rating_id, name) VALUES (?, ?)", 4, "R");
        jdbcTemplate.update("INSERT INTO rating (rating_id, name) VALUES (?, ?)", 5, "NC-17");
    }



    @Test
    public void testFindAll() {
        List<MpaRating> ratings = mpaRatingRepository.findAll();

        assertThat(ratings)
                .isNotNull()
                .isNotEmpty()
                .allSatisfy(rating -> {
                    assertThat(rating.getId()).isPositive();
                    assertThat(rating.getName()).isNotBlank();
                })
                .isSortedAccordingTo(Comparator.comparing(MpaRating::getId));

        
        assertThat(ratings)
                .extracting(MpaRating::getName)
                .doesNotHaveDuplicates();
    }

    @Test
    public void testFindById_WhenRatingExists() {
        
        List<MpaRating> allRatings = mpaRatingRepository.findAll();
        assumeThat(allRatings).isNotEmpty();

        MpaRating firstRating = allRatings.get(0);
        long existingRatingId = firstRating.getId();

        Optional<MpaRating> ratingOptional = mpaRatingRepository.findById(existingRatingId);

        assertThat(ratingOptional)
                .isPresent()
                .hasValueSatisfying(rating -> {
                    assertThat(rating.getId()).isEqualTo(existingRatingId);
                    assertThat(rating.getName()).isEqualTo(firstRating.getName());
                });
    }

    @Test
    public void testFindById_WithVariousExistingIds() {
        List<MpaRating> allRatings = mpaRatingRepository.findAll();
        assumeThat(allRatings).isNotEmpty();

        
        for (MpaRating expectedRating : allRatings) {
            Optional<MpaRating> ratingOptional = mpaRatingRepository.findById(expectedRating.getId());

            assertThat(ratingOptional)
                    .isPresent()
                    .hasValueSatisfying(rating -> {
                        assertThat(rating.getId()).isEqualTo(expectedRating.getId());
                        assertThat(rating.getName()).isEqualTo(expectedRating.getName());
                    });
        }
    }

    @Test
    public void testFindById_WhenRatingNotExists() {
        
        List<MpaRating> allRatings = mpaRatingRepository.findAll();
        long maxId = allRatings.stream()
                .mapToLong(MpaRating::getId)
                .max()
                .orElse(0);

        long nonExistingRatingId = maxId + 100;

        Optional<MpaRating> ratingOptional = mpaRatingRepository.findById(nonExistingRatingId);
        assertThat(ratingOptional).isNotPresent();
    }

    @Test
    public void testFindById_WithInvalidIds() {
        
        List<Long> invalidIds = List.of(0L, -1L, -100L, Long.MIN_VALUE);

        for (Long invalidId : invalidIds) {
            Optional<MpaRating> ratingOptional = mpaRatingRepository.findById(invalidId);
            assertThat(ratingOptional).isNotPresent();
        }
    }

    @Test
    public void testAllRatingNamesAreValid() {
        List<MpaRating> ratings = mpaRatingRepository.findAll();

        assertThat(ratings)
                .isNotEmpty()
                .allSatisfy(rating -> {
                    assertThat(rating.getName()).isNotBlank();
                    
                    assertThat(validRatingNames)
                            .withFailMessage("Rating name '%s' is not valid. Valid names are: %s",
rating.getName(), validRatingNames)
        .contains(rating.getName());
        });
        }

@Test
public void testRatingOrderAndConsistency() {
    List<MpaRating> ratings = mpaRatingRepository.findAll();

    if (ratings.size() > 1) {
        
        for (int i = 0; i < ratings.size() - 1; i++) {
            assertThat(ratings.get(i).getId())
                    .isLessThan(ratings.get(i + 1).getId());
        }
    }

    
    assertThat(ratings)
            .noneMatch(rating -> rating.getId() == null)
            .noneMatch(rating -> rating.getName() == null);
}

@Test
public void testRepositoryReturnsSameResultsForMultipleCalls() {
    
    List<MpaRating> firstCall = mpaRatingRepository.findAll();
    List<MpaRating> secondCall = mpaRatingRepository.findAll();

    assertThat(firstCall)
            .hasSameSizeAs(secondCall)
            .containsExactlyElementsOf(secondCall);
}

@Test
public void testFindById_Consistency() {
    List<MpaRating> allRatings = mpaRatingRepository.findAll();
    assumeThat(allRatings).isNotEmpty();

    
    MpaRating expectedRating = allRatings.get(0);
    Optional<MpaRating> foundRating = mpaRatingRepository.findById(expectedRating.getId());

    assertThat(foundRating)
            .isPresent()
            .hasValueSatisfying(rating -> {
                assertThat(rating.getId()).isEqualTo(expectedRating.getId());
                assertThat(rating.getName()).isEqualTo(expectedRating.getName());
            });
}

@Test
public void testNoInvalidRatingNamesInDatabase() {
    List<MpaRating> ratings = mpaRatingRepository.findAll();

    
    List<String> dbRatingNames = ratings.stream()
            .map(MpaRating::getName)
            .collect(Collectors.toList());

    
    assertThat(dbRatingNames)
            .isNotEmpty()
            .allSatisfy(ratingName ->
                    assertThat(validRatingNames)
                            .withFailMessage("Invalid rating name found in database: '%s'", ratingName)
                            .contains(ratingName)
            );
}
}