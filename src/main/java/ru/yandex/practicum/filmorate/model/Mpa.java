package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.AssertTrue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Mpa {
    private Long id;
    private String name;

    @AssertTrue(message = "Имя рейтинга должно быть значением из списка Ассоциации кинокомпаний")
    public boolean isValidRatingName() {
        List<String> ratingList = List.of("G", "PG", "PG-13", "R", "NC-17");
        return name != null && ratingList.contains(name);
    }
}
