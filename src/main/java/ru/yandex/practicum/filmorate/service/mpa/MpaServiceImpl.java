package ru.yandex.practicum.filmorate.service.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.MpaRepository;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.MpaMapper;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MpaServiceImpl implements MpaService {

    private final MpaRepository mpaRepository;

    @Override
    public List<MpaDto> getAllMpa() {
        return mpaRepository.findAll().stream()
                .map(MpaMapper::mapToMpaRatingDto)
                .collect(Collectors.toList());
    }

    @Override
    public MpaDto getMpaById(long id) {
        Mpa mpa = mpaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Рейтинг с id " + id + " не найден"));
        return MpaMapper.mapToMpaRatingDto(mpa);
    }
}
