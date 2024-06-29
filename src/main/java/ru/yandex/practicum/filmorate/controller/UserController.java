package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
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
        UserValidator.emailDoubleValidator(user);

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
        UserValidator.validateId(updatedUser);

        log.info("Проверка полей пользователя при его обновлении; {}", updatedUser.getLogin());
        UserValidator.validateUpdateFields(updatedUser);

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

    /**
     * UserValidator, утилитарный класс для валидации полей пользователей.
     */
    static final class UserValidator {

        private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);

        static void emailDoubleValidator(User user) {
            for (Long id : users.keySet()) {
                User middleUser = users.get(id);
                if (user.getEmail().equals(middleUser.getEmail())) {
                    throw new ValidationException("Этот имейл уже используется");
                }
            }
        }

        static void validateId(User user) {
            if (user.getId() == null) {
                throw new ValidationException("Id должен быть указан");
            }
        }

        static void validateUpdateFields(User updatedUser) {

            if (!users.containsKey(updatedUser.getId())) {
                throw new ValidationException("Польователь с id = " + updatedUser.getId() + " не найден");
            }

            //проверка на дубликат email при обновлении пользователей
            if (!updatedUser.getEmail().equals(users.get(updatedUser.getId()).getEmail())) {
                for (Long id : users.keySet()) {
                    User middleUser = users.get(id);
                    if (updatedUser.getEmail().equals(middleUser.getEmail())) {
                        throw new ValidationException("Имейл " + updatedUser.getEmail() + " уже присвоен другому " +
                                "пользователю: " + middleUser.getLogin());
                    }
                }
            }

            User oldUser = users.get(updatedUser.getId());

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
        }
    }
}

