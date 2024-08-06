package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FieldsValidatorService;
import ru.yandex.practicum.filmorate.service.UserFieldsDbValidatorService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Репозиторий для работы с пользователями в базе данных.
 * Реализует интерфейс UserStorage и предоставляет методы для выполнения операций CRUD с пользователями.
 */
@Slf4j
@Repository
@Qualifier("UserDbStorage")
public class UserDbStorage extends BaseRepository<User> implements UserStorage {

    // SQL-запросы
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM USERS WHERE USER_ID = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM USERS";
    private static final String INSERT_QUERY = "INSERT INTO USERS(USER_NAME, EMAIL, LOGIN, BIRTHDAY)" +
            "VALUES (?,?,?,?)";
    private static final String UPDATE_QUERY = "UPDATE USERS SET USER_NAME = ?, EMAIL = ?, LOGIN = ?, BIRTHDAY = ? " +
            "WHERE USER_ID = ?";
    private static final String FIND_FRIENDS_BY_ID = "SELECT FRIEND_ID AS USER_ID, EMAIL, LOGIN, USER_NAME, BIRTHDAY " +
            "FROM FRIENDSHIP INNER JOIN USERS ON FRIENDSHIP.FRIEND_ID = USERS.USER_ID WHERE FRIENDSHIP.USER_ID = ?";
    private static final String FIND_FRIENDS_IDS = "SELECT FRIEND_ID FROM FRIENDSHIP WHERE USER_ID = ?";
    private static final String INSERT_FRIEND_QUERY = "INSERT INTO FRIENDSHIP (USER_ID, FRIEND_ID) VALUES (?, ?)";
    private static final String DELETE_FRIEND_QUERY = "DELETE FROM FRIENDSHIP WHERE USER_ID = ? AND FRIEND_ID = ?";

    /**
     * Конструктор для инициализации UserDbStorage.
     *
     * @param jdbc   JdbcTemplate для выполнения SQL-запросов.
     * @param mapper RowMapper для преобразования строк результата SQL-запроса в объекты User.
     */
    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    private final UserFieldsDbValidatorService userDbValidator = new UserFieldsDbValidatorService(jdbc, mapper);

    /**
     * Получает всех пользователей из базы данных.
     *
     * @return Список всех пользователей.
     */
    @Override
    public List<User> getAll() {
        List<User> users = findMany(FIND_ALL_QUERY);
        for (User user : users) {
            user.setFriends(getFriendsSet(user.getId()));
        }
        return users;
    }

    /**
     * Создает нового пользователя в базе данных.
     *
     * @param user Пользователь, которого нужно создать.
     * @return Созданный пользователь с установленным идентификатором.
     */
    @Override
    public User createUser(User user) {
        userDbValidator.checkUserFieldsOnCreate(user);
        long id = insertWithGenId(
                INSERT_QUERY,
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getBirthday()
        );
        user.setId(id);
        return user;
    }

    /**
     * Обновляет существующего пользователя в базе данных.
     *
     * @param updatedUser Пользователь с обновленными данными.
     * @return Обновленный пользователь.
     */
    @Override
    public User update(User updatedUser) {
        log.info("Проверка наличия id пользователя в запросе: {}.", updatedUser.getLogin());
        FieldsValidatorService.validateUserId(updatedUser);
        userDbValidator.checkUserFieldsOnUpdate(updatedUser);
        update(
                UPDATE_QUERY,
                updatedUser.getName(),
                updatedUser.getEmail(),
                updatedUser.getLogin(),
                updatedUser.getBirthday(),
                updatedUser.getId()
        );
        updatedUser.setFriends(getFriendsSet(updatedUser.getId()));
        log.info("Пользователя с именем: {} обновлены.", updatedUser.getName());

        return updatedUser;
    }

    /**
     * Добавляет пользователя в друзья.
     *
     * @param userId   Идентификатор пользователя, который добавляет друга.
     * @param friendId Идентификатор пользователя, который будет добавлен в друзья.
     * @throws NotFoundException Если один из пользователей не найден.
     */
    public void addFriend(Long userId, Long friendId) {
        log.info("Проверка существования пользователей: {} и {}", userId, friendId);
        findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id " + userId +
                " не найден"));
        findById(friendId).orElseThrow(() -> new NotFoundException("Пользователь с id " + friendId + " не найден"));
        insert(INSERT_FRIEND_QUERY, userId, friendId);
        log.info("Пользователь с id {} добавил в друзья пользователя с id {}.", userId, friendId);
    }

    /**
     * Получает список друзей пользователя.
     *
     * @param id Идентификатор пользователя.
     * @return Список друзей пользователя.
     * @throws NotFoundException Если пользователь не найден.
     */
    public List<User> getUserFriends(Long id) {
        findById(id).orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
        List<User> friends = findMany(FIND_FRIENDS_BY_ID, id);
        for (User friend : friends) {
            friend.setFriends(getFriendsSet(friend.getId()));
        }
        return friends;

    }

    /**
     * Вспомогательный метод для получения коллекции идентификаторов друзей пользователя.
     *
     * @param id Идентификатор пользователя.
     * @return Множество идентификаторов друзей.
     */
    private Set<Long> getFriendsSet(Long id) {
        return new HashSet<>(findManyInstances(FIND_FRIENDS_IDS, Long.class, id));
    }

    /**
     * Удаляет пользователя из друзей.
     *
     * @param userId   Идентификатор пользователя, который удаляет друга.
     * @param friendId Идентификатор пользователя, который будет удален из друзей.
     * @throws NotFoundException Если один из пользователей не найден.
     */
    public void deleteFriend(Long userId, Long friendId) {
        log.info("Проверка существования пользователей: {} и {}", userId, friendId);
        findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id " + userId +
                " не найден"));
        findById(friendId).orElseThrow(() -> new NotFoundException("Пользователь с id " + friendId + " не найден"));
        deleteByTwoIds(DELETE_FRIEND_QUERY, userId, friendId);
        log.info("Пользователь с id {} удален из друзей пользователя с id {}.", userId, friendId);
    }

    /**
     * Находит пользователя по его идентификатору.
     *
     * @param id Идентификатор пользователя.
     * @return Опциональный пользователь, если найден, иначе пустой Optional.
     */
    @Override
    public Optional<User> findById(Long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    /**
     * Получает пользователя по его идентификатору.
     *
     * @param id Идентификатор пользователя.
     * @return Пользователь с указанным идентификатором.
     * @throws NotFoundException Если пользователь не найден.
     */
    public User getUserById(Long id) {
        User user = findById(id).orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
        user.setFriends(getFriendsSet(id));
        return user;
    }

    /**
     * Получает список общих друзей между двумя пользователями.
     *
     * @param userId  Идентификатор первого пользователя.
     * @param otherId Идентификатор второго пользователя.
     * @return Список общих друзей.
     */
    public List<User> getCommonFriends(Long userId, Long otherId) {
        Set<Long> userFriendsSet = getUserById(userId).getFriends();
        Set<Long> otherUserFriendsSet = getUserById(otherId).getFriends();

        userFriendsSet.retainAll(otherUserFriendsSet);

        return userFriendsSet.stream()
                .map(this::getUserById)
                .collect(Collectors.toList());
    }
}
