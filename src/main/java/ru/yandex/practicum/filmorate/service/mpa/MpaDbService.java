package ru.yandex.practicum.filmorate.service.mpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.Collection;

@Service
@Slf4j
public class MpaDbService implements MpaService {
    private final MpaStorage mpaStorage;

    @Autowired
    public MpaDbService(@Qualifier("mpaDbStorage") MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    @Override
    public Collection<Mpa> getAllMpa() {
        log.info("Запрос на получение всех рейтингов");
        return mpaStorage.getAllMpa();
    }

    @Override
    public Mpa getById(Long id) {
        return mpaStorage.getById(id).orElseThrow(() -> {
            NotFoundException e = new NotFoundException("Рейтинг не найден");
            log.error(e.getMessage());
            return e;
        });
    }
}
