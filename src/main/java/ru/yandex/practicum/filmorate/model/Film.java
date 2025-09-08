package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import ru.yandex.practicum.filmorate.annotation.EqualOrAfter;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@EqualsAndHashCode(of = "id")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Film {
    public static final LocalDate EARLIEST_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    public static final int MAX_DESCRIPTION_LENGTH = 200;
    private final Set<Long> likes = new HashSet<>();
    private Long id;
    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;
    @Size(max = MAX_DESCRIPTION_LENGTH, message = "Максимальная длина описания - " + MAX_DESCRIPTION_LENGTH + " символов")
    private String description;
    private Duration duration;
    @EqualOrAfter()
    private LocalDate releaseDate;
    @NotNull
    private Long rating;
    private List<Long> genres = new ArrayList<>();


    @AssertTrue(message = "Продолжительность фильма должна быть положительным числом")
    private boolean isValidDuration() {
        return duration != null && duration.isPositive();
    }

/*    @AssertTrue(message = "Рейтинг должен быть значением из списка Ассоциации кинокомпаний")
    private boolean isValidRating() {
        List<String> ratingList = List.of("G", "PG", "PG-13", "R", "NC-17");
        return rating != null && ratingList.contains(this.rating);
    }*/

    @JsonProperty("duration")
    public void setDurationFromMinutes(long minutes) {
        setDuration(Duration.ofMinutes(minutes));
    }

    @JsonProperty("duration")
    public long getDurationInMinutes() {
        return getDuration().toMinutes();
    }

    @JsonIgnore
    public Duration getDuration() {
        return duration;
    }

    @JsonIgnore
    public void setDuration(Duration duration) {
        this.duration = duration;
    }
}
