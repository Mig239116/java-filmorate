package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmDbStorage.class, FilmRowMapper.class})
@Sql(scripts = {"/schema.sql", "/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class FilmDbStorageTests {
    private final FilmDbStorage filmStorage;

    @Test
    public void checkGetAllFilms() {
        List<Film> films = filmStorage.getAllFilms().stream().toList();
        assertEquals(3, films.size());
    }

    @Test
    public void checkGetById() {
        Film film = filmStorage.getById(1L).orElseThrow();
        assertThat(film).hasFieldOrPropertyWithValue("name", "film1");
        assertThat(film).hasFieldOrPropertyWithValue("description", "film1");
        assertThat(film).hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(1980, 1, 1));
        assertThat(film).hasFieldOrPropertyWithValue("duration", Duration.ofMinutes(100));
    }

    @Test
    public void checkAddFilm() {
        Film film = new Film();
        film.setName("name");
        film.setDescription("description");
        film.setDuration(Duration.ofMinutes(110));
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        Mpa mpa = new Mpa();
        mpa.setId(1);
        film.setMpa(mpa);
        filmStorage.addFilm(film);
        assertTrue(filmStorage.getById(4L).isPresent());
        Film film2 = filmStorage.getById(4L).orElseThrow();
        assertThat(film2).hasFieldOrPropertyWithValue("id", 4L);
        assertThat(film).hasFieldOrPropertyWithValue("name", "name");
        assertThat(film).hasFieldOrPropertyWithValue("description", "description");
        assertThat(film).hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(2000, 1, 1));
        assertThat(film).hasFieldOrPropertyWithValue("duration", Duration.ofMinutes(110));
    }

    @Test
    public void checkUpdateFilm() {
        Film film = new Film();
        film.setId(1L);
        film.setName("name");
        film.setDescription("description");
        film.setDuration(Duration.ofMinutes(110));
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        Mpa mpa = new Mpa();
        mpa.setId(1);
        film.setMpa(mpa);
        filmStorage.updateFilm(film);
        assertTrue(filmStorage.getById(1L).isPresent());
        Film film2 = filmStorage.getById(1L).orElseThrow();
        assertThat(film2).hasFieldOrPropertyWithValue("id", 1L);
        assertThat(film).hasFieldOrPropertyWithValue("name", "name");
        assertThat(film).hasFieldOrPropertyWithValue("description", "description");
        assertThat(film).hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(2000, 1, 1));
        assertThat(film).hasFieldOrPropertyWithValue("duration", Duration.ofMinutes(110));
    }

    @Test
    public void checkAddDeleteLike() {
        filmStorage.addLike(1L, 1L);
        Film film = filmStorage.getById(1L).orElseThrow();
        assertEquals(1, film.getLikes().size());
        assertEquals(1L, film.getLikes().stream().toList().get(0));
        filmStorage.deleteLike(1L, 1L);
        Film film1 = filmStorage.getById(1L).orElseThrow();
        assertEquals(0, film1.getLikes().size());
    }

    @Test
    public void checkGetPopularFilms() {
        filmStorage.addLike(1L, 1L);
        filmStorage.addLike(1L, 2L);
        filmStorage.addLike(1L, 3L);
        filmStorage.addLike(2L, 1L);
        filmStorage.addLike(2L, 2L);
        List<Film> films = filmStorage.getPopularFilms(3).stream().toList();
        assertEquals(3, films.size());
        assertEquals(1L, films.get(0).getId());
        assertEquals(2L, films.get(1).getId());
        assertEquals(3L, films.get(2).getId());
        films = filmStorage.getPopularFilms(2).stream().toList();
        assertEquals(2, films.size());
    }
}
