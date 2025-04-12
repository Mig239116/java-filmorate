package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {

    Collection<Film> getAllFilms();

    Film addFilm(Film film);

    Film updateFilm(Film newFilm);

    void addLike(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);

    Collection<Film> getPopularFilms(Integer count);

    Optional<Film> getById(Long id);
}
