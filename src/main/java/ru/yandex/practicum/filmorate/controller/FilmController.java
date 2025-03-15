package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.time.Month.DECEMBER;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
            validateFilm(film);
            film.setId(getNextId());
            films.put(film.getId(), film);
            log.info("Добавлен фильм " + film.getName() + "c ид " + film.getId());
            return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film newFilm) {
        if (newFilm.getId() == null) {
            log.error("Id должен быть указан");
            throw new ValidationException("Id должен быть указан");
        }
        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());
                validateFilm(newFilm);
                oldFilm.setDescription(newFilm.getDescription());
                oldFilm.setName(newFilm.getName());
                oldFilm.setDuration(newFilm.getDuration());
                oldFilm.setReleaseDate(newFilm.getReleaseDate());
                log.info("Обновлен фильм " + newFilm.getName() + "c ид " + newFilm.getId());
                return oldFilm;
        }
        log.error("Фильм с id = " + newFilm.getId() + " не найден!");
        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден!");
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("Название не может быть пустым");
            throw new ValidationException("Название не может быть пустым");
        }
        if (film.getDescription() != null
                && !film.getDescription().isBlank()
                && film.getDescription().length() > 200) {
            log.error("Максимальная длина описания — 200 символов");
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate() == null
                || film.getReleaseDate().isBefore(LocalDate.of(1895, DECEMBER, 28))) {
            throw new ValidationException("Дата релиза не раньше 28 декабря 1895 года");
        }

        if (film.getDuration() == null
                || film.getDuration().toMinutes() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительным числом.");
        }
    }

    private Long getNextId() {
        return films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0) + 1;
    }
}
