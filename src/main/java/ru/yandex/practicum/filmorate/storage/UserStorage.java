package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

/**
 * Интерфейс для хранения и управления пользователями.
 *
 * @see User
 */
@Component

public interface UserStorage {

    /**
     * getAll возвращает коллекцию всех пользователей.
     *
     * @return коллекция всех пользователей
     */
    Collection<User> getAll();

    /**
     * createUser создает нового пользователя.
     *
     * @param user объект пользователя для создания
     * @return созданный объект пользователя
     */
    User createUser(User user);

    /**
     * update обновляет информацию о существующем пользователе.
     *
     * @param updatedUser объект пользователя с обновленной информацией
     * @return обновленный объект пользователя
     */
    User update(User updatedUser);

    /**
     * findById находит пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя
     * @return объект пользователя, если он найден, иначе пустой Optional
     */
    Optional<User> findById(Long id);
}
