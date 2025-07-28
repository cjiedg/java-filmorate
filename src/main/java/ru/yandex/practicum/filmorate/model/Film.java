package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import ru.yandex.practicum.filmorate.annotation.EqualOrAfter;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(of = "id")
@Builder
@Getter
public class Film {
    public static final LocalDate EARLIEST_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    public static final int MAX_DESCRIPTION_LENGTH = 200;
    private final Set<Long> likes = new HashSet<>();
    private Long id;
    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;
    @Size(max = MAX_DESCRIPTION_LENGTH, message = "Максимальная длина описания - " + MAX_DESCRIPTION_LENGTH + " символов")
    private String description;
    @EqualOrAfter()
    private LocalDate releaseDate;
    private Duration duration;

    @AssertTrue(message = "Продолжительность фильма должна быть положительным числом")
    public boolean isValidDuration() {
        if (duration == null) return true;
        return duration.isPositive();
    }

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
