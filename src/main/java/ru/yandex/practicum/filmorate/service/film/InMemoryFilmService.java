package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

@Service
@Slf4j
public class InMemoryFilmService implements FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public InMemoryFilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    @Override
    public Collection<Film> getAllFilms() {
        log.info("Запрос на получение всех фильмов");
        return filmStorage.getAllFilms();
    }

    @Override
    public Film addFilm(Film film) {
        log.info("Запрос на получение фильма: " + film);
        return filmStorage.addFilm(film);
    }

    @Override
    public Film updateFilm(Film film) {
        if (filmStorage.getById(film.getId()).isPresent()) {
            log.debug("Запрос на обновление фильма: " + film);
            return filmStorage.updateFilm(film);
        }
        log.error("Фильм с " + film.getId() + "не найден");
        throw new NotFoundException("Фильм с " + film.getId() + "не найден");
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        if (filmStorage.getById(filmId).isEmpty()) {
            log.error("Фильм с " + filmId + "не найден");
            throw new NotFoundException("Фильм с " + filmId + "не найден");
        } else if (userStorage.getById(userId).isEmpty()) {
            log.error("Пользователь с " + userId + "не найден");
            throw new NotFoundException("Пользователь с " + userId + "не найден");
        } else {
            log.info("Пользователь " + userId + "поставил лайк фильму" + filmId);
            filmStorage.addLike(filmId, userId);
        }
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        if (filmStorage.getById(filmId).isEmpty()) {
            log.error("Фильм с " + filmId + "не найден");
            throw new NotFoundException("Фильм с " + filmId + "не найден");
        } else if (userStorage.getById(userId).isEmpty()) {
            log.error("Пользователь с " + userId + "не найден");
            throw new NotFoundException("Пользователь с " + userId + "не найден");
        } else {
            log.info("Пользователь " + userId + "удалил лайк фильму" + filmId);
            filmStorage.deleteLike(filmId, userId);
        }
    }

    @Override
    public Collection<Film> getPopularFilms(Integer count) {
        log.info("Запрошен список из " + count + " популярных фильмов");
        return filmStorage.getPopularFilms(count);
    }

    @Override
    public Film getByID(Long id) {
        return filmStorage.getById(id).orElseThrow(
                () -> new NotFoundException("Фильм с id = " + id + "не найден")
        );
    }
}
