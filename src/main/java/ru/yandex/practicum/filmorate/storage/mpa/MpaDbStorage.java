package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.Collection;
import java.util.Optional;

@Repository
public class MpaDbStorage extends BaseStorage<Mpa> implements MpaStorage {
    private static final String FIND_ALL = "SELECT * FROM mpa";
    private static final String FIND_ONE = "SELECT * FROM mpa WHERE id = ?";

    public MpaDbStorage(JdbcTemplate jdbc, MpaRowMapper mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<Mpa> getAllMpa() {
        return findMany(FIND_ALL);
    }

    @Override
    public Optional<Mpa> getById(Long id) {
        return findOne(FIND_ONE, id);
    }

}
