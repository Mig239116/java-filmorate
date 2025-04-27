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
import java.util.stream.Collectors;

@Repository
public class UserDbStorage extends BaseStorage<User> implements UserStorage {
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

    private static final String FIND_FRIENDS_BY_USERID = """
            SELECT *
            FROM user_friend
            WHERE user_id IN (%s)
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

    private static final String FIND_ALL_FRIENDS = """
            SELECT
                *
            FROM users
            WHERE id in (%s)
            """;

    public UserDbStorage(JdbcTemplate jdbc,
                         UserRowMapper mapper) {
        super(jdbc, mapper);
    }

    public Collection<User> getAllUsers() {
        List<User> users = findMany(FIND_ALL);
        Map<Long, List<Long>> userFriend = getFriendsByUserId(
                users.stream()
                        .map(User::getId)
                        .toList()
        );
        for (User user: users) {
            List<Long> friends = userFriend.getOrDefault(user.getId(), Collections.emptyList());
            user.setFriends(friends != null ? new HashSet<>(friends) : new HashSet<>());
        }
        return users;
    }

    private Map<Long, List<Long>> getFriendsByUserId(List<Long> userIds) {
        if (userIds.isEmpty()) {
            return new HashMap<Long, List<Long>>();
        }
        String params = String.join(",", Collections.nCopies(userIds.size(), "?"));
        String sql = String.format(FIND_FRIENDS_BY_USERID, params);
        return jdbc.query(
                sql,
                userIds.toArray(),
                rs -> {
                    Map<Long, List<Long>> result = new HashMap<>();
                    while (rs.next()) {
                        Long userId = rs.getLong("user_id");
                        Long friendId = rs.getLong("friend_id");
                        result.computeIfAbsent(userId, k -> new ArrayList<>()).add(friendId);
                    }
                    return result;
                }
        );
    }

    public Optional<User> getById(Long userId) {
        User user = findOne(FIND_ONE, userId).orElseThrow(
                () -> new NotFoundException("Пользователь " + userId + " не найден")
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
        User user = getById(userId).orElseThrow(() -> new NotFoundException("Пользователь " + userId + " не найден"));
        if (!user.getFriends().contains(friendId)) {
            update(
                    ADD_FRIEND,
                    userId,
                    friendId
            );
        }
    }

    public void deleteFriend(Long userId, Long friendId) {
        User user = getById(userId).orElseThrow(() -> new NotFoundException("Пользователь " + userId + " не найден"));
        if (user.getFriends().contains(friendId)) {
            update(
                    DELETE_FRIEND,
                    userId,
                    friendId
            );
        }
    }

    public Collection<User> getAllFriends(Long userId) {
        User user = getById(userId).orElseThrow(() -> new NotFoundException("Пользователь " + userId + " не найден"));
        List<Long> friendsIds = new ArrayList<>(user.getFriends());
        String params = friendsIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        String sql = String.format(FIND_ALL_FRIENDS, params);
        List<User> friends = findMany(sql);
        Map<Long, List<Long>> friendFriend = getFriendsByUserId(
                friends.stream()
                        .map(User::getId)
                        .toList()
        );
        for (User friend: friends) {
            List<Long> friendsOfFriends = friendFriend.getOrDefault(friend.getId(), Collections.emptyList());
            friend.setFriends(friendsOfFriends != null ? new HashSet<>(friendsOfFriends) : new HashSet<>());
        }
        return friends;
    }

    public Collection<User> getCommonFriends(Long userId, Long otherUserId) {
        User user = getById(userId).orElseThrow(() -> new NotFoundException("Пользователь " + userId + " не найден"));
        User otherUser = getById(otherUserId).orElseThrow(() -> new NotFoundException("Пользователь " + otherUserId + " не найден"));
        Set<Long> intersection = new HashSet<>(user.getFriends());
        boolean isIntersected = intersection.retainAll(otherUser.getFriends());
        if (isIntersected) {
            String params = intersection.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
            String sql = String.format(FIND_ALL_FRIENDS, params);
            List<User> commonFriends = findMany(sql);
            Map<Long, List<Long>> friendFriend = getFriendsByUserId(
                    commonFriends.stream()
                            .map(User::getId)
                            .toList()
            );
            for (User friend: commonFriends) {
                List<Long> friendsOfFriends = friendFriend.getOrDefault(friend.getId(), Collections.emptyList());
                friend.setFriends(friendsOfFriends != null ? new HashSet<>(friendsOfFriends) : new HashSet<>());
            }
            return commonFriends;
        }
        return null;
    }
}
