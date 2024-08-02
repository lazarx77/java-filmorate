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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
//    private final Map<Long, User> users = null;

    private static final String FIND_FRIENDS_BY_ID = "SELECT FRIEND_ID FROM FRIENDSHIP WHERE USER_ID = ?";

    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }
    private final UserFieldsDbValidatorService userDbValidator = new UserFieldsDbValidatorService(jdbc, mapper);

    @Override
    public List<User> getAll() {
        return findMany(FIND_ALL_QUERY);
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


//    private void checkUserFieldsOnCreate(User user) {
//        log.info("Проверка полей пользователя при его создании; {}", user.getLogin());
//        String FIND_BY_EMAIL = "SELECT * FROM USERS WHERE EMAIL =?";
//        findOne(FIND_BY_EMAIL, user.getEmail());
//        if (findOne(FIND_BY_EMAIL, user.getEmail()).isPresent()) {
//            throw new ValidationException("Этот имейл " + user.getEmail() + " уже используется");
//        }
//        String FIND_BY_LOGIN = "SELECT * FROM USERS WHERE LOGIN = ?";
//        if (findOne(FIND_BY_LOGIN, user.getLogin()).isPresent()) {
//            throw new ValidationException("Пользователь с таким логином " + user.getLogin() + " уже существует.");
//        }
//    }

//    private void checkUserFieldsOnUpdate(User updatedUser) {
//        log.info("Проверка полей пользователя при его обновлении; {}", updatedUser.getLogin());
//        if (findById(updatedUser.getId()).isEmpty()) {
//            throw new NotFoundException("Польователь с id = " + updatedUser.getId() + " не найден");
//        }
//
//        String sql = "SELECT * FROM USERS WHERE EMAIL = ? AND USER_ID != ?";
//        if (findOne(sql, updatedUser.getEmail(), updatedUser.getId()).isPresent()) {
//            throw new ValidationException("Этот имейл " + updatedUser.getEmail() + " уже используется");
//            }
//        }


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
        log.info("Пользователя с именем: {} обновлены.", updatedUser.getName());

        return updatedUser;
    }

    public void addFriend(Long userId, Long friendId) {
        // Проверка существования пользователей
        log.info("Проверка существования пользователей: {} и {}", userId, friendId);
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
        return findById(userId).isPresent();
    }

    private void addFriendship(Long userId, Long friendId, boolean status) {
        String INSERT_FRIEND_QUERY = "INSERT INTO FRIENDSHIP (USER_ID, FRIEND_ID, STATUS) VALUES (?, ?, ?)";
        insert(INSERT_FRIEND_QUERY, userId, friendId, status);
    }

    private void updateFriendshipStatus(Long userId, Long friendId) {
        String UPDATE_STATUS_QUERY = "UPDATE FRIENDSHIP SET STATUS = ? WHERE USER_ID = ? AND FRIEND_ID = ?";
        insert(UPDATE_STATUS_QUERY, true, userId, friendId);
    }

    @Override
    public Optional<User> findById(Long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    public List<Long> getUserFriends(Long id) {
        List<Long> friends = new ArrayList<>();
        jdbc.query(FIND_FRIENDS_BY_ID, rs -> {
            while (rs.next()) {
                friends.add(rs.getLong("FRIEND_ID"));
            }
            return friends;
        }, id);
        return friends;
    }
}
