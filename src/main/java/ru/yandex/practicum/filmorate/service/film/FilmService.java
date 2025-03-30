package ru.yandex.practicum.filmorate.service.film;

import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;

import static java.time.Month.DECEMBER;


public interface FilmService {
    Collection<Film> getAllFilms();
    Film addFilm(Film film);
    Film updateFilm(Film film);
    void addLike(Long filmId, Long userId);
    void deleteLike(Long filmId, Long userId);
    Collection<Film> getPopularFilms(Integer count);
    Film getByID(Long id);
}
