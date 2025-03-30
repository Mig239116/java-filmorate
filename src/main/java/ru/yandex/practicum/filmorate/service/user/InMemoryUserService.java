package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

@Service
@Slf4j
public class InMemoryUserService implements UserService {
    private final UserStorage userStorage;

    @Autowired
    public InMemoryUserService(UserStorage userStorage) {
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
        if (userStorage.getById(user.getId()).isPresent()) {
            log.info("Запрос на обновление пользователя " + user);
            return userStorage.updateUser(user);
        }
        log.error("Пользователь с " + user.getId() + "не найден");
        throw new NotFoundException("Пользователь с " + user.getId() + "не найден");
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        if (userStorage.getById(userId).isPresent()
                && userStorage.getById(friendId).isPresent()) {
            log.info("Пользователь " + userId + "добавил в друзья пользователя " + friendId);
            userStorage.addFriend(userId, friendId);
        } else {
            log.error("Пользователи не найдены");
            throw new NotFoundException("Пользователи не найдены");
        }
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        if (userStorage.getById(userId).isPresent()
                && userStorage.getById(friendId).isPresent()) {
            log.info("Пользователь " + userId + "удалил из друзей пользователя " + friendId);
            userStorage.deleteFriend(userId, friendId);
        } else {
            throw new NotFoundException("Пользователи не найдены");
        }
    }

    @Override
    public Collection<User> getAllFriends(Long userId) {
        if (userStorage.getById(userId).isPresent()) {
            log.info("Запрошен список всех друзей пользователя " + userId);
            return userStorage.getAllFriends(userId);
        }
        log.error("Пользователи не найдены");
        throw new NotFoundException("Пользователи не найдены");
    }

    @Override
    public Collection<User> getCommonFriends(Long userId, Long otherUserId) {
        if (userStorage.getById(userId).isPresent()
                && userStorage.getById(otherUserId).isPresent()) {
            log.info("Запрошен список всех друзей пользователей " + userId + " и " + otherUserId);
            return userStorage.getCommonFriends(userId, otherUserId);
        }
        log.error("Пользователи не найдены");
        throw new NotFoundException("Пользователи не найдены");
    }

    @Override
    public User getById(Long userId) {
        return userStorage.getById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с " + userId + "не найден")
        );
    }


}
