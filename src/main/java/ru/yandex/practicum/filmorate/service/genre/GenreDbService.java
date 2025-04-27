package ru.yandex.practicum.filmorate.service.genre;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.Collection;

@Service
@Slf4j
public class GenreDbService implements GenreService {
    private final GenreStorage genreStorage;

    @Autowired
    public GenreDbService(@Qualifier("genreDbStorage") GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    @Override
    public Collection<Genre> getAllGenres() {
        log.info("Запрос на получение всех жанров");
        return genreStorage.getAllGenres();
    }

    @Override
    public Genre getById(Long id) {
        return genreStorage.getById(id).orElseThrow(() -> {
            NotFoundException e = new NotFoundException("Жанр " + id + " не найден");
            log.error(e.getMessage());
            return e;
        });
    }
}
