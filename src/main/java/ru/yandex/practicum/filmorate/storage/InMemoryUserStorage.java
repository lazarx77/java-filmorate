package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FieldsValidatorService;

import java.util.*;

/**
 * InMemoryUserStorage.
 */
@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private static final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public User createUser(User user) {

        log.info("проверка email на дубликат пользователя при его добавлении: {}", user.getLogin());
        FieldsValidatorService.emailDoubleValidator(user, users);

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

    @Override
    public User update(User updatedUser) {
        // проверяем необходимые условия

        log.info("Проверка наличия id пользователя в запросе: {}.", updatedUser.getLogin());
        FieldsValidatorService.validateUserId(updatedUser);

        log.info("Проверка полей пользователя при его обновлении; {}", updatedUser.getLogin());
        FieldsValidatorService.validateUpdateUserFields(updatedUser, users);

        users.put(updatedUser.getId(), updatedUser);
        log.info("Пользователя с именем: {} обновлены.", updatedUser.getName());

        return users.get(updatedUser.getId());
    }

    //метод для нахождения пользователя по id
    @Override
    public Optional<User> findById(Long id) {
        return users.values().stream().filter(u -> u.getId().equals(id)).findAny();
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
