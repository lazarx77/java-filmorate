package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * UserController.
 */
@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getAll() {
        return users.values();
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        for (Long id : users.keySet()) {
            User middleUser = users.get(id);
            if (user.getEmail().equals(middleUser.getEmail())) {
                throw new ValidationException("Этот имейл уже используется");
            }
        }

        if (user.getName() == null) {
            user.setName(user.getLogin());
        }

        // формируем дополнительные данные
        user.setId(getNextId());
        // сохраняем нового пользователя в памяти приложения
        users.put(user.getId(), user);
        log.info("Пользователь с именем: {} был добавлен в картотеку.", user.getName());

        return users.get(user.getId());
    }

    @PutMapping
    public User update(@Valid @RequestBody User updatedUser) {
        // проверяем необходимые условия
        if (updatedUser.getId() == null) {
            throw new ValidationException("Id должен быть указан");
        }
        if (users.containsKey(updatedUser.getId())) {
            User oldUser = users.get(updatedUser.getId());
            users.remove(updatedUser.getId());

            for (Long id : users.keySet()) {
                User middleUser = users.get(id);
                if (updatedUser.getEmail().equals(middleUser.getEmail())) {
                    users.put(updatedUser.getId(), oldUser);
                    throw new ValidationException("Этот имейл уже используется");
                }
            }

            if (updatedUser.getLogin() == null) {
                updatedUser.setLogin(oldUser.getLogin());
            }

            if (updatedUser.getName() == null) {
                updatedUser.setName(oldUser.getName());
            }
            if (updatedUser.getEmail() == null) {
                updatedUser.setEmail(oldUser.getEmail());
            }


            if (updatedUser.getBirthday() == null) {
                updatedUser.setBirthday(oldUser.getBirthday());
            }

            users.put(updatedUser.getId(), updatedUser);
            log.info("Пользователя с именем: {} обновлены.", updatedUser.getName());
        } else {
            throw new ValidationException("Польователь с id = " + updatedUser.getId() + " не найден");
        }

        return users.get(updatedUser.getId());
    }

    // вспомогательный метод для генерации идентификатора нового пользователя
    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
