package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @Override
    public User addUser(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        users.replace(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> getById(Long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        if (!users.get(userId).getFriends().contains(friendId)) {
            users.get(userId).addFriend(friendId);
            users.get(friendId).addFriend(userId);
        }
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        if (users.get(userId).getFriends().contains(friendId)) {
            users.get(userId).deleteFriend(friendId);
            users.get(friendId).deleteFriend(userId);
        }
    }

    @Override
    public Collection<User> getAllFriends(Long userId) {
        return users.values().stream()
                        .filter(user -> users.get(userId).getFriends().contains(user.getId()))
                        .collect(Collectors.toList());
    }

    @Override
    public Collection<User> getCommonFriends(Long userId, Long otherUserId) {
        return users.values().stream()
                .filter(user -> users.get(userId).getFriends().contains(user.getId()) &&
                        users.get(otherUserId).getFriends().contains(user.getId()))
                .collect(Collectors.toList());
    }

    private Long getNextId() {
        return users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0) + 1;
    }
}
