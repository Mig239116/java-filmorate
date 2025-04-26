package ru.yandex.practicum.filmorate.service.mpa;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

public interface MpaService {
    Collection<Mpa> getAllMpa();
    Mpa getById(Long id);
}
