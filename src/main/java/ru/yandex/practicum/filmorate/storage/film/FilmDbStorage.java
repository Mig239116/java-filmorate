package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.jdbc.core.*;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class FilmDbStorage extends BaseStorage<Film> implements FilmStorage {
    private static final String FIND_ALL = """
            SELECT
                f.id, f.name, f.description, f.release_date, f.duration, f.mpa_id,
                m.name AS mpa_name
            FROM films f
            LEFT JOIN mpa m ON f.mpa_id = m.id
            """;

    private static final String FIND_GENRES = """
            SELECT *
            FROM genres
            WHERE id IN (
                SELECT genre_id
                FROM genre_film
                WHERE film_id = ?
            )
            """;

    private static final String FIND_LIKES = """
            SELECT user_id
            FROM likes
            WHERE film_id = ?
            """;

    private static final String FIND_ONE = """
            SELECT
                f.id, f.name, f.description, f.release_date, f.duration, f.mpa_id,
                m.name AS mpa_name
            FROM films f
            LEFT JOIN mpa m ON f.mpa_id = m.id
            WHERE f.id = ?
            """;

    private static final String INSERT_FILM = """
            INSERT INTO films (name, description, release_date, duration, mpa_id)
            VALUES (?, ?, ?, ?, ?)
            """;

    private static final String INSERT_GENRE = """
            INSERT INTO genre_film (film_id, genre_id)
            VALUES (?, ?)
            """;

    private static final String UPDATE_FILM = """
            UPDATE films
            SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ?
            WHERE id = ?
            """;

    private static final String DELETE_GENRE = """
            DELETE FROM genre_film
            WHERE film_id = ? AND genre_id = ?
            """;

    private static final String  ADD_LIKE = """
            INSERT INTO likes (film_id, user_id)
            VALUES (?, ?)
            """;

    private static final String DELETE_LIKE = """
            DELETE FROM likes
            WHERE film_id = ? AND user_id = ?
            """;

    private static final String POPULAR_FILMS = """
            SELECT
                f.id, f.name, f.description, f.release_date, f.duration, f.mpa_id,
                m.name AS mpa_name,
                COUNT(l.user_id) AS likes_count
            FROM films f
            LEFT JOIN mpa m ON f.mpa_id = m.id
            LEFT JOIN likes l ON f.id = l.film_id
            GROUP BY f.id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.name
            ORDER BY likes_count DESC
            LIMIT ?
            """;

    private static final String FIND_GENRES_BY_FILMID = """
            SELECT fg.film_id, g.*
            FROM genre_film fg
            JOIN genres g ON fg.genre_id = g.id
            WHERE fg.film_id IN (%s)
            """;

    private static final String FIND_LIKES_BY_FILMID = """
            SELECT *
            FROM likes
            WHERE film_id IN (%s)
            """;

    public FilmDbStorage(JdbcTemplate jdbc,
                         FilmRowMapper mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<Film> getAllFilms() {
        List<Film> films = findMany(FIND_ALL);
        Map<Long, List<Genre>> filmGenres = getGenresByFilmId(
                films.stream()
                        .map(Film::getId)
                        .toList()
        );
        Map<Long, List<Long>> filmLikes = getLikesByFilmId(
                films.stream()
                        .map(Film::getId)
                        .toList()
        );
        for (Film film: films) {
            List<Genre> genres = filmGenres.getOrDefault(film.getId(), Collections.emptyList());
            film.setGenres(genres != null ? new HashSet<>(genres) : new HashSet<>());
            List<Long> likes = filmLikes.getOrDefault(film.getId(), Collections.emptyList());
            film.setLikes(likes != null ? new HashSet<>(likes) : new HashSet<>());
        }
        return films;
    }

    private Map<Long, List<Long>> getLikesByFilmId(List<Long> filmIds) {
        if (filmIds.isEmpty()) {
            return new HashMap<Long, List<Long>>();
        }
        String params = String.join(",", Collections.nCopies(filmIds.size(), "?"));
        String sql = String.format(FIND_LIKES_BY_FILMID, params);
        return jdbc.query(
                sql,
                filmIds.toArray(),
                rs -> {
                    Map<Long, List<Long>> result = new HashMap<>();
                    while (rs.next()) {
                        Long filmId = rs.getLong("film_id");
                        Long userId = rs.getLong("user_id");
                        result.computeIfAbsent(filmId, k -> new ArrayList<>()).add(userId);
                    }
                    return result;
                }
        );
    }

    private Map<Long, List<Genre>> getGenresByFilmId(List<Long> filmIds) {
        if (filmIds.isEmpty()) {
            return new HashMap<Long, List<Genre>>();
        }
        String params = String.join(",", Collections.nCopies(filmIds.size(), "?"));
        String sql = String.format(FIND_GENRES_BY_FILMID, params);
        return jdbc.query(
                sql,
                filmIds.toArray(),
                rs -> {
                    Map<Long, List<Genre>> result = new HashMap<>();
                    while (rs.next()) {
                        Long filmId = rs.getLong("film_id");
                        Genre genre = new Genre(rs.getLong("id"), rs.getString("name"));
                        result.computeIfAbsent(filmId, k -> new ArrayList<>()).add(genre);
                    }
                    return result;
                }
        );
    }

    @Override
    public Optional<Film> getById(Long id) {
        Film film = findOne(FIND_ONE, id).orElseThrow(() -> new NotFoundException("Фильм " + id + " не найден"));
        film.setGenres(new HashSet<>(jdbc.query(FIND_GENRES, new GenreRowMapper(), film.getId())));
        film.setLikes(new HashSet<>(
                jdbc.query(FIND_LIKES, new RowMapper<Long>() {
                    @Override
                    public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return rs.getLong("user_id");
                    }
                }, film.getId())
        ));
        return Optional.ofNullable(film);
    }

    @Override
    public Film addFilm(Film film) {
        if (film.getMpa() != null) {
            MpaDbStorage mpaStorage = new MpaDbStorage(jdbc, new MpaRowMapper());
            if (mpaStorage.getById(film.getMpa().getId()).isEmpty()) {
                throw new NotFoundException("Такого рейтинга " + film.getMpa().getId() + " не существует");
            }
        }
        long id = insert(
                INSERT_FILM,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration().toMinutes(),
                film.getMpa().getId()
        );
        film.setId(id);
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            batchProcessGenres(INSERT_GENRE, id, new ArrayList<>(film.getGenres()
                    .stream()
                    .map(Genre::getId)
                    .collect(Collectors.toList()))
            );
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        update(
                UPDATE_FILM,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            Optional<Film> optional = getById(film.getId());
            Film oldFilm = optional.orElseThrow(() -> new NotFoundException("Фильм " + film.getId() + " не найден"));
            Set<Long> addDifference = new HashSet<>(
                    film.getGenres()
                            .stream()
                            .map(Genre::getId)
                            .collect(Collectors.toSet())
            );
            boolean additionCheck = true;
            if (oldFilm.getGenres() != null && !oldFilm.getGenres().isEmpty()) {
                additionCheck = addDifference.removeAll(oldFilm.getGenres());
                Set<Long> deleteDifference = new HashSet<>(
                        oldFilm.getGenres()
                                .stream()
                                .map(Genre::getId)
                                .collect(Collectors.toSet())
                );
                boolean deletionCheck = deleteDifference.removeAll(film.getGenres());
                if (deletionCheck) {
                    batchProcessGenres(DELETE_GENRE, film.getId(), new ArrayList<>(deleteDifference));
                }
            }
            if (additionCheck) {
                batchProcessGenres(INSERT_GENRE, film.getId(), new ArrayList<>(addDifference));
            }
        }
        return film;
    }

    public void addLike(Long filmId, Long userId) {
        Film film = getById(filmId).orElseThrow(() -> new NotFoundException("Фильм " + filmId + " не найден"));
        if (!film.getLikes().contains(userId)) {
            update(
                    ADD_LIKE,
                    filmId,
                    userId
                    );
        }
    }

    public void deleteLike(Long filmId, Long userId) {
        Film film = getById(filmId).orElseThrow(() -> new NotFoundException("Фильм " + filmId + " не найден"));
        if (film.getLikes().contains(userId)) {
            update(
                    DELETE_LIKE,
                    filmId,
                    userId
            );
        }
    }

    public Collection<Film> getPopularFilms(Integer count) {
        List<Film> films = findMany(POPULAR_FILMS, count);
        Map<Long, List<Genre>> filmGenres = getGenresByFilmId(
                films.stream()
                        .map(Film::getId)
                        .toList()
        );
        Map<Long, List<Long>> filmLikes = getLikesByFilmId(
                films.stream()
                        .map(Film::getId)
                        .toList()
        );
        for (Film film: films) {
            List<Genre> genres = filmGenres.getOrDefault(film.getId(), Collections.emptyList());
            film.setGenres(genres != null ? new HashSet<>(genres) : new HashSet<>());
            List<Long> likes = filmLikes.getOrDefault(film.getId(), Collections.emptyList());
            film.setLikes(likes != null ? new HashSet<>(likes) : new HashSet<>());
        }
        return films;
    }

    private void batchProcessGenres(String query, Long filmId, List<Long> genres) {
        if (genres == null || genres.isEmpty()) {
            return;
        }
        validateGenresExist(genres);
        jdbc.batchUpdate(
                query,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setLong(1, filmId);
                        ps.setLong(2, genres.get(i));
                    }

                    @Override
                    public int getBatchSize() {
                        return genres.size();
                    }
                }
        );
    }

    private void validateGenresExist(List<Long> genreIds) {
        if (genreIds != null && !genreIds.isEmpty()) {
            String existingGenresQuery = "SELECT id FROM genres WHERE id IN (" +
                    String.join(",", Collections.nCopies(genreIds.size(), "?")) + ")";
            List<Long> existingIds = jdbc.queryForList(existingGenresQuery, genreIds.toArray(), Long.class);

            if (existingIds.size() != genreIds.size()) {
                genreIds.removeAll(existingIds);
                throw new NotFoundException("Жанры с ID " + genreIds + " не найдены");
            }
        }
    }


}
