package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static java.time.Month.DECEMBER;

@Component
public class InMemoryFilmStorage implements FilmStorage{

    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    @Override
    public Film addFilm(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Optional<Film> getById(Long filmId) {
        return Optional.ofNullable(films.get(filmId));
    }

    @Override
    public Film updateFilm(Film newFilm) {
        films.replace(newFilm.getId(), newFilm);
        return newFilm;
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        films.get(filmId).addLike(userId);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        films.get(filmId).deleteLike(userId);
    }

    @Override
    public Collection<Film> getPopularFilms(Integer count) {
        return films.values().stream()
                .sorted(Comparator.comparingInt(Film::getLikesCount).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }

    private Long getNextId() {
        return films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0) + 1;
    }
}
