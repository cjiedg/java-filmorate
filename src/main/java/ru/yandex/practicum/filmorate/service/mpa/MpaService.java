package ru.yandex.practicum.filmorate.service.mpa;


import ru.yandex.practicum.filmorate.dto.MpaDto;

import java.util.List;

public interface MpaService {
    List<MpaDto> getAllMpa();

    MpaDto getMpaById(long id);
}
