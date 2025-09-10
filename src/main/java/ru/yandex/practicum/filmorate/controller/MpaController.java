package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dal.MpaRepository;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.MpaMapper;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/mpa")
public class MpaController {
    private final MpaRepository mpaRatingRepository;

    public MpaController(MpaRepository mpaRatingRepository) {
        this.mpaRatingRepository = mpaRatingRepository;
    }

    @GetMapping
    public List<MpaDto> getAllMpaRatings() {
        return mpaRatingRepository.findAll().stream()
                .map(MpaMapper::mapToMpaRatingDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public MpaDto getMpaRatingById(@PathVariable long id) {
        Mpa rating = mpaRatingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Рейтинг с id " + id + " не найден"));
        return MpaMapper.mapToMpaRatingDto(rating);
    }
}