package ru.yandex.practicum.filmorate.service.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserService {
    Collection<User> getAllUsers();
    User addUser(User user);
    User updateUser(User user);
    void addFriend(Long userId, Long friendId);
    void deleteFriend(Long userId, Long friendId);
    Collection<User> getAllFriends(Long userId);
    Collection<User> getCommonFriends(Long userId, Long otherUserId);
    User getById(Long userId);

}
