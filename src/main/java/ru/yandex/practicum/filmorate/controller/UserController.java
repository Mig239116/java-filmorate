package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @PostMapping
    public User addUser(@RequestBody User user) {
        String validation = validateUser(user);
        if (validation != null) {
            log.error(validation);
            throw new ValidationException(validation);
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Добавлен пользователь " + user.getName() + "c ид " + user.getId());
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User newUser) {
        if (newUser.getId() == null) {
            log.error("Id должен быть указан");
            throw new NotFoundException("Id должен быть указан");
        }
        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            String validation = validateUser(newUser);
            if (validation != null) {
                log.error(validation);
                throw new ValidationException(validation);
            }
            oldUser.setEmail(newUser.getEmail());
            oldUser.setBirthday(newUser.getBirthday());
            oldUser.setName(newUser.getName());
            oldUser.setLogin(newUser.getLogin());
            log.info("Обновлен пользователь " + newUser.getName() + "c ид " + newUser.getId());
            return oldUser;
        }
        log.error("Пользователь с id = " + newUser.getId() + " не найден!");
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден!");
    }

    private String validateUser(User user) {
        if (user.getEmail() == null ||
                user.getEmail().isBlank() ||
                !user.getEmail().contains("@")) {
            return "Электронная почта не может быть пустой и должна содержать символ @";
        }
        if (user.getLogin() == null ||
                user.getLogin().isBlank() ||
                user.getLogin().contains(" ")) {
            return "Логин не может быть пустым и содержать пробелы";
        }
        if (user.getName() == null || user.getName().isBlank()) {
            log.warn("Имя не указано. Будет использован логин вместо имени.");
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            return "Дата рождения не может быть в будущем";
        }
        return null;
    }

    private Long getNextId() {
        return users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0) + 1;
    }


}
