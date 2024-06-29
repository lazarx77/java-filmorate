package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FieldsValidator;

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

    private static final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getAll() {
        return users.values();
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {

        log.info("проверка email на дубликат пользователя при его добавлении: {}", user.getLogin());
        FieldsValidator.emailDoubleValidator(user, users);

        // формируем дополнительные данные
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }

        user.setId(getNextId());

        // сохраняем нового пользователя в памяти приложения
        users.put(user.getId(), user);
        log.info("Пользователь с именем: {} был добавлен в картотеку.", user.getName());

        return users.get(user.getId());
    }

    @PutMapping
    public User update(@Valid @RequestBody User updatedUser) {
        // проверяем необходимые условия

        log.info("Проверка наличия Id пользователя в запросе: {}.", updatedUser.getLogin());
        FieldsValidator.validateUserId(updatedUser);

        log.info("Проверка полей пользователя при его обновлении; {}", updatedUser.getLogin());
        FieldsValidator.validateUpdateUserFields(updatedUser, users);

        users.put(updatedUser.getId(), updatedUser);
        log.info("Пользователя с именем: {} обновлены.", updatedUser.getName());

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
