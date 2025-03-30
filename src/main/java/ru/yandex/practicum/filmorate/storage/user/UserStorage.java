package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    Collection<User> getAllUsers();
    User addUser(User user);
    User updateUser(User user);
    void addFriend(Long userId, Long friendId);
    void deleteFriend(Long userId, Long friendId);
    Collection<User> getAllFriends(Long userId);
    Collection<User> getCommonFriends(Long userId, Long otherUserId);
    Optional<User> getById(Long userId);
}
