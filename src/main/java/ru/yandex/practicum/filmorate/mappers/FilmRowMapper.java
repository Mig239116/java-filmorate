package ru.yandex.practicum.filmorate.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

@Component
public class FilmRowMapper implements RowMapper<Film> {

    @Override
    public Film mapRow(ResultSet resultSet, int rowInt) throws SQLException {
        Film film = new Film();
        Set<Genre> genres = new HashSet<>();
        Set<Long> likes = new HashSet<>();

        film.setId(resultSet.getLong("id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));
        film.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
        film.setDuration(Duration.ofMinutes(resultSet.getInt("duration")));

        if (resultSet.getObject("mpa_id") != null) {
            Mpa mpa = new Mpa();
            mpa.setId(resultSet.getLong("mpa_id"));
            mpa.setName(resultSet.getString("mpa_name"));
            film.setMpa(mpa);
        } else {
            throw new SQLDataException("MPA не может быть null");
        }
        return film;
    }
}
