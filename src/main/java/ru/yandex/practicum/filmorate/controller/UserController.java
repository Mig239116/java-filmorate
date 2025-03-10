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
        try {
            validateUser(user);
            user.setId(getNextId());
            users.put(user.getId(), user);
            log.info("Добавлен пользователь " + user.getName() + "c ид " + user.getId());
            return user;
        } catch (ValidationException e) {
            log.error(e.getMessage());
            throw new ValidationException(e.getMessage());
        }
    }

    @PutMapping
    public User updateUser(@RequestBody User newUser) {
        if (newUser.getId() == null) {
            log.error("Id должен быть указан");
            throw new ValidationException("Id должен быть указан");
        }
        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            try {
                validateUser(newUser);
                oldUser.setEmail(newUser.getEmail());
                oldUser.setBirthday(newUser.getBirthday());
                oldUser.setName(newUser.getName());
                oldUser.setLogin(newUser.getLogin());
                log.info("Обновлен пользователь " + newUser.getName() + "c ид " + newUser.getId());
                return oldUser;
            } catch (ValidationException e) {
                log.error(e.getMessage());
                throw new ValidationException(e.getMessage());
            }
        }
        log.error("Пользователь с id = " + newUser.getId() + " не найден!");
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден!");
    }

    private void validateUser(User user) throws ValidationException {
        if (user.getEmail() == null ||
                user.getEmail().isBlank() ||
                !user.getEmail().contains("@")) {
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }
        if (user.getLogin() == null ||
                user.getLogin().isBlank() ||
                user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            log.warn("Имя не указано. Будет использован логин вместо имени.");
            user.setName(user.getLogin());
        }
        if (user.getBirthday() == null) {
            throw new ValidationException("Дата рождения обязательное поле");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }

    private Long getNextId() {
        return users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0) + 1;
    }


}
