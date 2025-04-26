package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

@Service
@Slf4j
public class UserDbService implements UserService{
    private final UserStorage userStorage;

    @Autowired
    public UserDbService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public Collection<User> getAllUsers() {
        log.info("Запрошен список всех пользователей");
        return userStorage.getAllUsers();
    }

    @Override
    public User addUser(User user) {
        log.info("Запрос на добавление пользователя " + user);
        return userStorage.addUser(user);
    }

    @Override
    public User updateUser(User user) {
        getUserById(user.getId());
        log.info("Запрос на обновление пользователя " + user);
        return userStorage.updateUser(user);

    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        getUserById(userId);
        getUserById(friendId);
        if (userId == friendId) return;
        log.info("Пользователь " + userId + "добавил в друзья пользователя " + friendId);
        userStorage.addFriend(userId, friendId);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        getUserById(userId);
        getUserById(friendId);
        log.info("Пользователь " + userId + "удалил из друзей пользователя " + friendId);
        userStorage.deleteFriend(userId, friendId);
    }

    @Override
    public Collection<User> getAllFriends(Long userId) {
        getUserById(userId);
        log.info("Запрошен список всех друзей пользователя " + userId);
        return userStorage.getAllFriends(userId);
    }

    @Override
    public Collection<User> getCommonFriends(Long userId, Long otherUserId) {
        getUserById(userId);
        getUserById(otherUserId);
        log.info("Запрошен список всех друзей пользователей " + userId + " и " + otherUserId);
        return userStorage.getCommonFriends(userId, otherUserId);

    }

    @Override
    public User getById(Long userId) {
        return getUserById(userId);
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
}
