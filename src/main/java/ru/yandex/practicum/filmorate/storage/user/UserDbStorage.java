package ru.yandex.practicum.filmorate.storage.user;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class UserDbStorage extends BaseStorage<User> implements UserStorage{
    private static final String FIND_ALL = """
            SELECT
                *
            FROM users
            """;

    private static final String FIND_FRIENDS = """
            SELECT friend_id
            FROM user_friend
            WHERE user_id = ?
            """;
    private static final String FIND_ONE = """
            SELECT
                *
            FROM users
            WHERE id = ?
            """;

    private static final String INSERT_USER = """
            INSERT INTO users (email, login, name, birthday)
            VALUES (?, ?, ?, ?)
            """;

    private static final String UPDATE_USER = """
            UPDATE users
            SET email = ?, login = ?, name = ?, birthday = ?
            WHERE id = ?
            """;

    private static final String  ADD_FRIEND = """
            INSERT INTO user_friend (user_id, friend_id)
            VALUES (?, ?)
            """;

    private static final String DELETE_FRIEND = """
            DELETE FROM user_friend
            WHERE user_id = ? AND friend_id = ?
            """;

    public UserDbStorage(JdbcTemplate jdbc,
                         UserRowMapper mapper) {
        super(jdbc, mapper);
    }

    public Collection<User> getAllUsers() {
        List<User> users = findMany(FIND_ALL);
        for (User user: users) {
            user.setFriends(new HashSet<>(
                    jdbc.query(
                            FIND_FRIENDS,
                            new RowMapper<Long>() {
                                @Override
                                public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
                                    return rs.getLong("friend_id");
                                }
                            },
                            user.getId())
            ));
        }
        return users;
    }

    public Optional<User> getById(Long userId) {
        User user = findOne(FIND_ONE, userId).orElseThrow(
                () -> new NotFoundException("Пользователь не найден")
        );
        user.setFriends(new HashSet<>(
                jdbc.query(
                        FIND_FRIENDS,
                        new RowMapper<Long>() {
                            @Override
                            public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
                                return rs.getLong("friend_id");
                            }
                        },
                        user.getId())
        ));
        return Optional.ofNullable(user);
    }

    public User addUser(User user) {
        long id = insert(
                INSERT_USER,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );
        user.setId(id);
        return user;
    }

    public User updateUser(User user) {
        update(
                UPDATE_USER,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );
        return user;
    }

    public void addFriend(Long userId, Long friendId) {
        User user = getById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        if (!user.getFriends().contains(friendId)) {
            update(
                    ADD_FRIEND,
                    userId,
                    friendId
            );
        }
    }

    public void deleteFriend(Long userId, Long friendId) {
        User user = getById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        if (user.getFriends().contains(friendId)) {
            update(
                    DELETE_FRIEND,
                    userId,
                    friendId
            );
        }
    }

    public Collection<User> getAllFriends(Long userId) {
        User user = getById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        List<User> friends = new ArrayList<>();
        for (Long id: user.getFriends()) {
            friends.add(getById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден")));
        }
        return friends;
    }

    public Collection<User> getCommonFriends(Long userId, Long otherUserId) {
        User user = getById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        User otherUser = getById(otherUserId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Set<Long> intersection = new HashSet<>(user.getFriends());
        boolean isIntersected = intersection.retainAll(otherUser.getFriends());
        if (isIntersected) {
            List<User> commonFriends = new ArrayList<>();
            for (Long id: intersection) {
                commonFriends.add(getById(id).orElseThrow(() -> new NotFoundException("Фильм не найден")));
            }
            return commonFriends;
        }
        return null;
    }
}
