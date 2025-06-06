package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

@Service
@Slf4j
public class InMemoryFilmService implements FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public InMemoryFilmService(
            @Qualifier("inMemoryFilmStorage") FilmStorage filmStorage,
            @Qualifier("inMemoryUserStorage") UserStorage userStorage) {
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
        getFilmById(film.getId());
        log.debug("Запрос на обновление фильма: " + film);
        return filmStorage.updateFilm(film);
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        getFilmById(filmId);
        getUserById(userId);
        log.info("Пользователь " + userId + "поставил лайк фильму" + filmId);
        filmStorage.addLike(filmId, userId);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        getFilmById(filmId);
        getUserById(userId);
        log.info("Пользователь " + userId + "удалил лайк фильму" + filmId);
        filmStorage.deleteLike(filmId, userId);
    }

    @Override
    public Collection<Film> getPopularFilms(Integer count) {
        log.info("Запрошен список из " + count + " популярных фильмов");
        return filmStorage.getPopularFilms(count);
    }

    @Override
    public Film getByID(Long id) {
        return getFilmById(id);
    }

    private User getUserById(Long userId) {
        return userStorage.getById(userId).orElseThrow(
                () -> {
                    NotFoundException e = new NotFoundException("Пользователь с " + userId + " не найден");
                    log.error(e.getMessage());
                    return e;
                }
        );
    }

    private Film getFilmById(Long filmId) {
        return filmStorage.getById(filmId).orElseThrow(
                () -> {
                    NotFoundException e = new NotFoundException("Фильм с id = " + filmId + " не найден");
                    log.error(e.getMessage());
                    return e;
                }
        );
    }
}
