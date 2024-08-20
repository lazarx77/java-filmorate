package ru.yandex.practicum.filmorate.dal;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Репозиторий для работы с пользователями в базе данных.
 * Реализует интерфейс UserStorage и предоставляет методы для выполнения операций CRUD с пользователями.
 */
@Repository
@Qualifier("UserDbStorage")
public class UserDbStorage extends BaseRepository<User> implements UserStorage {
    private final FilmDbStorage filmDbStorage = new FilmDbStorage(super.jdbc,new FilmRowMapper());
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
    private static final String GET_USER_LIKES_QUERY = "WITH prep AS (\n" +
            "\t\t\t\tSELECT l1.USER_ID,\n" +
            "\t\t\t\t       COUNT(*) cnt\n" +
            "\t\t\t\t  FROM likes l1\n" +
            "\t\t\t\t INNER JOIN likes l2\n" +
            "\t\t\t\t    ON l2.FILM_ID = l1.film_id\n" +
            "\t\t\t\t   AND l2.USER_ID = ?\n" +
            "\t\t\t\t WHERE l1.user_id != ?\n" +
            "\t\t\t\t GROUP BY l1.user_id\n" +
            "\t\t\t\t ORDER BY count(*) desc\n" +
            "\t\t\t )\n" +
            "    SELECT f.*\n" +
            "      FROM LIKES l1 \n" +
            "     INNER JOIN prep P\n" +
            "        ON p.USER_id = l1.USER_ID\n" +
            "      LEFT JOIN likes l2 \n" +
            "        ON L2.FILM_ID = l1.FILM_ID \n" +
            "       AND l2.USER_ID = ?\n" +
            "     INNER JOIN films f\n" +
            "        ON f.FILM_ID = l1.FILM_ID \n" +
            "     WHERE l2.film_id IS null\n" +
            "     ORDER BY P.cnt desc";

    /**
     * Конструктор для инициализации UserDbStorage.
     *
     * @param jdbc   JdbcTemplate для выполнения SQL-запросов.
     * @param mapper RowMapper для преобразования строк результата SQL-запроса в объекты User.
     */
    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

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
        update(
                UPDATE_QUERY,
                updatedUser.getName(),
                updatedUser.getEmail(),
                updatedUser.getLogin(),
                updatedUser.getBirthday(),
                updatedUser.getId()
        );
        updatedUser.setFriends(getFriendsSet(updatedUser.getId()));

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
        insert(INSERT_FRIEND_QUERY, userId, friendId);
    }

    /**
     * Получает список друзей пользователя.
     *
     * @param id Идентификатор пользователя.
     * @return Список друзей пользователя.
     * @throws NotFoundException Если пользователь не найден.
     */
    public List<User> getUserFriends(Long id) {
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
        deleteByTwoIds(DELETE_FRIEND_QUERY, userId, friendId);
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

    public List<Film> getRecommendations(long id) {
        System.out.println(filmDbStorage.findMany(GET_USER_LIKES_QUERY,id,id,id));
        return filmDbStorage.findMany(GET_USER_LIKES_QUERY,id,id,id);

    }
}
