package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FieldsValidatorService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
@Qualifier("UserDbStorage")
public class UserDbStorage extends BaseRepository<User> implements UserStorage {

    private static final String FIND_BY_ID_QUERY = "SELECT * FROM USERS WHERE id = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM USERS";
    private static final String INSERT_QUERY = "INSERT INTO USERS(USERNAME, EMAIL, LOGIN, BIRTHDAY)" +
            "VALUES (?,?,?,?)";
    private static final String UPDATE_QUERY = "UPDATE USERS SET USERNAME = ?, EMAIL = ?, LOGIN = ?, BIRTHDAY = ? " +
            "WHERE id = ?";
    private final Map<Long, User> users = null;

    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper, User.class);
    }

    @Override
    public List<User> getAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public User createUser(User user) {
        long id = insert(
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

        log.info("Проверка полей пользователя при его обновлении; {}", updatedUser.getLogin());

        String sql = "SELECT COUNT(*) FROM USERS WHERE LOGIN = ?";

        int count = jdbc.queryForObject(sql, Integer.class, updatedUser.getLogin());

        if (count > 0) {
            throw new IllegalArgumentException("Пользователь с таким логином уже существует.");
        }

        update(
                UPDATE_QUERY,
                updatedUser.getName(),
                updatedUser.getEmail(),
                updatedUser.getLogin(),
                updatedUser.getBirthday(),
                updatedUser.getId()
        );
        log.info("Пользователя с именем: {} обновлены.", updatedUser.getName());

        return updatedUser;
    }

    public void addFriend(Long userId, Long friendId) {
        // Проверка существования пользователей
        if (!isUserExist(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }

        if (!isUserExist(friendId)) {
            throw new NotFoundException("Пользователь с id " + friendId + " не найден");
        }

        // Проверка статуса дружбы
        String FRIEND_CHECK_QUERY = "SELECT STATUS FROM FRIENDSHIP WHERE USER_ID = ? AND FRIEND_ID = ?";
        Boolean status = jdbc.queryForObject(FRIEND_CHECK_QUERY, Boolean.class, friendId, userId);

        // Запросы на добавление или обновление дружбы
        if (status == null) {
            addFriendship(userId, friendId, false);
            addFriendship(friendId, userId, true);
        } else if (Boolean.TRUE.equals(status)) {
            updateFriendshipStatus(userId, friendId);
        }

        log.info("Пользователь с id {} добавил в друзья пользователя с id {}.", userId, friendId);
    }

    private boolean isUserExist(Long userId) {
        String query = "SELECT COUNT(*) FROM USERS WHERE ID = ?";
        return jdbc.queryForObject(query, Integer.class, userId) > 0;
    }

    private void addFriendship(Long userId, Long friendId, boolean status) {
        String INSERT_FRIEND_QUERY = "INSERT INTO FRIENDSHIP (USER_ID, FRIEND_ID, STATUS) VALUES (?, ?, ?)";
        update(INSERT_FRIEND_QUERY, userId, friendId, status);
    }

    private void updateFriendshipStatus(Long userId, Long friendId) {
        String UPDATE_STATUS_QUERY = "UPDATE FRIENDSHIP SET STATUS = ? WHERE USER_ID = ? AND FRIEND_ID = ?";
        update(UPDATE_STATUS_QUERY, true, userId, friendId);
    }




//    public boolean isValidUser(User user) {
//        log.info("Проверка наличия id пользователя в запросе: {}.", user.getLogin());
//        FieldsValidatorService.validateUserId(user);
//        if (findById(user.getId()).isEmpty()) {
//            throw new NotFoundException("Польователь с id = " + user.getId() + " не найден");
//        }
//
//    }


    @Override
    public Optional<User> findById(Long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }
}
