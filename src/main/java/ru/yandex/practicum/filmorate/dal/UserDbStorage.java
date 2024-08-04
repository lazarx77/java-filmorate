package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FieldsValidatorService;
import ru.yandex.practicum.filmorate.service.UserFieldsDbValidatorService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Slf4j
@Repository
@Qualifier("UserDbStorage")
public class UserDbStorage extends BaseRepository<User> implements UserStorage {

    private static final String FIND_BY_ID_QUERY = "SELECT * FROM USERS WHERE USER_ID = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM USERS";
    private static final String INSERT_QUERY = "INSERT INTO USERS(USER_NAME, EMAIL, LOGIN, BIRTHDAY)" +
            "VALUES (?,?,?,?)";
    private static final String UPDATE_QUERY = "UPDATE USERS SET USER_NAME = ?, EMAIL = ?, LOGIN = ?, BIRTHDAY = ? " +
            "WHERE USER_ID = ?";

    private static final String FIND_FRIENDS_BY_ID = "SELECT FRIEND_ID AS USER_ID, EMAIL, LOGIN, USER_NAME, BIRTHDAY " +
            "FROM FRIENDSHIP INNER JOIN USERS ON FRIENDSHIP.FRIEND_ID = USERS.USER_ID WHERE FRIENDSHIP.USER_ID = ?";
    private static final String FIND_FRIENDS_IDS = "SELECT FRIEND_ID FROM FRIENDSHIP WHERE USER_ID = ?";

    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    private final UserFieldsDbValidatorService userDbValidator = new UserFieldsDbValidatorService(jdbc, mapper);

    @Override
    public List<User> getAll() {
        List<User> users = findMany(FIND_ALL_QUERY);
        for (User user : users) {
            user.setFriends(getFriendsSet(user.getId()));
        }

        return users;
    }

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

    public void addFriend(Long userId, Long friendId) {
        // Проверка существования пользователей
        log.info("Проверка существования пользователей: {} и {}", userId, friendId);
        User user = findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        if (findById(friendId).isEmpty()) {
            throw new NotFoundException("Пользователь с id " + friendId + " не найден");
        }
        String INSERT_FRIEND_QUERY = "INSERT INTO FRIENDSHIP (USER_ID, FRIEND_ID) VALUES (?, ?)";
        insert(INSERT_FRIEND_QUERY, userId, friendId);
        user.setFriends(getFriendsSet(userId));
        log.info("Пользователь с id {} добавил в друзья пользователя с id {}.", userId, friendId);
    }

    public List<User> getUserFriends(Long id) {
        List<User> friends = findMany(FIND_FRIENDS_BY_ID, id);
        for (User friend : friends) {
            friend.setFriends(getFriendsSet(friend.getId()));
        }
        return friends;

    }

    //Вспомогательный метод для получения коллекции друзей пользователя
    private Set<Long> getFriendsSet(Long id) {
        return new HashSet<>(findManyIds(FIND_FRIENDS_IDS, id));
    }

    public void deleteFriend(Long userId, Long friendId) {
        User user = findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
//        User friend = findById(friendId).orElseThrow(() -> new NotFoundException("Пользователь с id " + friendId + " не найден"));
//        log.info("Проверка существования пользователей: {} и {}", userId, friendId);
//        if (findById(userId).isEmpty()) {
//            throw new NotFoundException("Пользователь с id " + userId + " не найден");
//        }
//
        String FIND_FRIEND_STATUS_QUERY = "SELECT COUNT(*) FROM FRIENDSHIP WHERE USER_ID = ? AND FRIEND_ID = ?";
        int friendshipStatus = jdbc.queryForObject(FIND_FRIEND_STATUS_QUERY, Integer.class, userId, friendId);
        if (friendshipStatus == 0) {
            throw new NotFoundException("Пользователь с id " + userId + " не является другом для пользователя с id " + friendId);
        }
//        if (findById(friendId).isEmpty()) {
//            throw new NotFoundException("Пользователь с id " + friendId + " не найден");
//        }
        String DELETE_FRIEND_QUERY = "DELETE FROM FRIENDSHIP WHERE USER_ID = ? AND FRIEND_ID = ?";
        update(DELETE_FRIEND_QUERY, userId, friendId);
        user.setFriends(getFriendsSet(userId));
        log.info("Пользователь с id {} удален из друзей пользователя с id {}.", userId, friendId);
//        User user = findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
//        User friend = findById(friendId).orElseThrow(() -> new NotFoundException("Пользователь с id " + friendId + " не найден"));

    }

    @Override
    public Optional<User> findById(Long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    public User getUserById(Long id) {
        User user = findById(id).orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
        user.setFriends(getFriendsSet(id));
        return user;
    }
//    public List<Long> getUserFriends(Long id) {
//        List<Long> friends = new ArrayList<>();
//        jdbc.query(FIND_FRIENDS_BY_ID, rs -> {
//            while (rs.next()) {
//                friends.add(rs.getLong("FRIEND_ID"));
//            }
//            return friends;
//        }, id);
//        return friends;
//    }
}
