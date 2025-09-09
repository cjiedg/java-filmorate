package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dal.MpaRatingRepository;
import ru.yandex.practicum.filmorate.dto.MpaRatingDto;
import ru.yandex.practicum.filmorate.mapper.MpaMapper;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/mpa")
public class MpaController {
    private final MpaRatingRepository mpaRatingRepository;

    public MpaController(MpaRatingRepository mpaRatingRepository) {
        this.mpaRatingRepository = mpaRatingRepository;
    }

    @GetMapping
    public List<MpaRatingDto> getAllMpaRatings() {
        return mpaRatingRepository.findAll().stream()
                .map(MpaMapper::mapToMpaRatingDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public MpaRatingDto getMpaRatingById(@PathVariable long id) {
        MpaRating rating = mpaRatingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Рейтинг с id " + id + " не найден"));
        return MpaMapper.mapToMpaRatingDto(rating);
    }
}