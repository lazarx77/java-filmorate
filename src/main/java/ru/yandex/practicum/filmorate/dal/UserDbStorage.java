package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Repository
@Qualifier("UserDbStorage")
public class UserDbStorage extends BaseRepository<User> implements UserStorage {

    private static final String FIND_BY_ID_QUERY = "SELECT * FROM USERS WHERE id = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM USERS";
    private static final String INSERT_QUERY = "INSERT INTO USERS(USERNAME, EMAIL, LOGIN, BIRTHDAY)" +
            "VALUES (?,?,?,?)";
    private static final String UPDATE_QUERY = "UPDATE USERS SET USERNAME =?, EMAIL =?, LOGIN =?, BIRTHDAY =? " +
            "WHERE id = ?";

    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper, User.class);
    }

    @Override
    public Collection<User> getAll() {
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
        update(
                UPDATE_QUERY,
                updatedUser.getName(),
                updatedUser.getEmail(),
                updatedUser.getLogin(),
                updatedUser.getBirthday(),
                updatedUser.getId()
        );
//        updatedUser.setFriends();
        return updatedUser;
    }

    @Override
    public Optional<User> findById(Long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }
}
