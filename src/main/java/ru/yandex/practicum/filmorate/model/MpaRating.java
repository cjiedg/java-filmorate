package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.AssertTrue;
import lombok.Data;

import java.util.List;

@Data
public class MpaRating {
    private Long id;
    @AssertTrue
    private String name;

    @AssertTrue(message = "Имя рейтинга должно быть значением из списка Ассоциации кинокомпаний")
    private boolean isValidRatingName() {
        List<String> ratingList = List.of("G", "PG", "PG-13", "R", "NC-17");
        return name != null && ratingList.contains(this.name);
    }
}
