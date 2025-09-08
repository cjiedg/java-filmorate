package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.MpaRatingDto;
import ru.yandex.practicum.filmorate.model.MpaRating;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MpaMapper {
    public static MpaRatingDto mapToMpaRatingDto(MpaRating rating) {
        MpaRatingDto dto = new MpaRatingDto();
        dto.setId(rating.getId());
        dto.setName(rating.getName());
        return dto;
    }
}