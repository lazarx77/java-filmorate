package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.HistoryDbStorage;
import ru.yandex.practicum.filmorate.dal.UserDbStorage;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.enums.EventTypes;
import ru.yandex.practicum.filmorate.model.enums.OperationTypes;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для работы с пользователями в базе данных.
 * Предоставляет методы для создания, обновления, получения и управления друзьями пользователей.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDbService {

    private final UserFieldsDbValidatorService userDbValidator;
    private final UserDbStorage userDbStorage;
    private final HistoryDbStorage historyDbStorage;

    /**
     * Возвращает список всех пользователей.
     *
     * @return Список всех пользователей в базе данных.
     */
    public List<User> getAll() {
        return userDbStorage.getAll();
    }

    /**
     * Создает нового пользователя.
     *
     * @param user Пользователь, который необходимо создать.
     * @return Созданный пользователь с присвоенным идентификатором.
     * @throws ValidationException Если данные пользователя некорректны.
     */
    public User createUser(User user) {
        userDbValidator.checkUserFieldsOnCreate(user);
        return userDbStorage.createUser(user);
    }

    /**
     * Обновляет информацию о пользователе.
     *
     * @param updatedUser Пользователь с обновленными данными.
     * @return Обновленный пользователь.
     * @throws NotFoundException   Если пользователь с указанным идентификатором не найден.
     * @throws ValidationException Если данные пользователя некорректны.
     */
    public User update(User updatedUser) {
        log.info("Проверка наличия id пользователя в запросе: {}.", updatedUser.getLogin());
        FieldsValidatorService.validateUserId(updatedUser);
        userDbValidator.checkUserFieldsOnUpdate(updatedUser);
        log.info("Пользователя с именем: {} обновлены.", updatedUser.getName());

        return userDbStorage.update(updatedUser);
    }

    /**
     * Добавляет пользователя в друзья.
     *
     * @param userId   Идентификатор пользователя, который добавляет друга.
     * @param friendId Идентификатор пользователя, который добавляется в друзья.
     * @throws NotFoundException Если один из пользователей не найден.
     */
    public void addFriend(Long userId, Long friendId) {
        log.info("Проверка существования пользователей: {} и {}", userId, friendId);
        findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id " + userId +
                " не найден"));
        findById(friendId).orElseThrow(() -> new NotFoundException("Пользователь с id " + friendId + " не найден"));
        userDbStorage.addFriend(userId, friendId);
        log.info("Пользователь с id {} добавил в друзья пользователя с id {}.", userId, friendId);
        historyDbStorage.addEvent(Event.builder()
                .userId(userId)
                .timestamp(System.currentTimeMillis())
                .eventType(EventTypes.FRIEND)
                .operation(OperationTypes.ADD)
                .entityId(friendId)
                .build());
    }

    /**
     * Возвращает список друзей пользователя.
     *
     * @param id Идентификатор пользователя, для которого нужно получить список друзей.
     * @return Список друзей пользователя.
     * @throws NotFoundException Если пользователь с указанным идентификатором не найден.
     */
    public List<User> getUserFriends(Long id) {
        findById(id).orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
        return userDbStorage.getUserFriends(id);
    }

    /**
     * Удаляет пользователя из списка друзей.
     *
     * @param userId   Идентификатор пользователя, который удаляет друга.
     * @param friendId Идентификатор пользователя, который удаляется из друзей.
     * @throws NotFoundException Если один из пользователей не найден.
     */
    public void deleteFriend(Long userId, Long friendId) {
        log.info("Проверка существования пользователей: {} и {}", userId, friendId);
        findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id " + userId +
                " не найден"));
        findById(friendId).orElseThrow(() -> new NotFoundException("Пользователь с id " + friendId + " не найден"));
        userDbStorage.deleteFriend(userId, friendId);
        log.info("Пользователь с id {} удален из друзей пользователя с id {}.", userId, friendId);
        historyDbStorage.addEvent(Event.builder()
                .userId(userId)
                .timestamp(System.currentTimeMillis())
                .eventType(EventTypes.FRIEND)
                .operation(OperationTypes.REMOVE)
                .entityId(friendId)
                .build());
    }

    /**
     * Находит пользователя по его идентификатору.
     *
     * @param id Идентификатор пользователя.
     * @return Опциональный объект пользователя, если пользователь найден, иначе пустой объект.
     */
    protected Optional<User> findById(Long id) {
        return userDbStorage.findById(id);
    }

    /**
     * Получает пользователя по его идентификатору.
     *
     * @param id Идентификатор пользователя.
     * @return Пользователь с указанным идентификатором.
     * @throws NotFoundException Если пользователь с указанным идентификатором не найден.
     */
    public User getUserById(Long id) {
        return userDbStorage.getUserById(id);
    }

    /**
     * Возвращает список общих друзей между двумя пользователями.
     *
     * @param userId  Идентификатор первого пользователя.
     * @param otherId Идентификатор второго пользователя.
     * @return Список общих друзей между двумя пользователями.
     */
    public List<User> getCommonFriends(Long userId, Long otherId) {
        return userDbStorage.getCommonFriends(userId, otherId);
    }

    public List<Film> getRecommendations(long id) {
        return userDbStorage.getRecommendations(id);
    }

    /**
     * Удаляет пользователя и все связанные с ним записи из таблиц.
     *
     * @param userId Идентификатор пользователя.
     */
    public void deleteUser(long userId) {
        userDbStorage.deleteUser(userId);
        log.info("Пользователь с id {} удален.", userId);
    }
}
