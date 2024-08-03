package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Map;

@Slf4j
public class UserFieldsDbValidatorService extends BaseRepository<User> {

    private static final String EMAIL_VALIDATION_QUERY = "SELECT * FROM USERS WHERE EMAIL =?";

    public UserFieldsDbValidatorService(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

//    public static void emailDoubleValidator(User user, Map<Long, User> users) {
//        for (Long id : users.keySet()) {
//            User middleUser = users.get(id);
//            if (user.getEmail().equals(middleUser.getEmail())) {
//                throw new ValidationException("Этот имейл уже используется");
//            }
//        }
//    }

    public void checkUserFieldsOnUpdate(User updatedUser) {
        log.info("Проверка полей пользователя при его обновлении; {}", updatedUser.getLogin());
        if (findOne("SELECT * FROM USERS WHERE USER_ID = ?", updatedUser.getId()).isEmpty()) {
            throw new NotFoundException("Польователь с id = " + updatedUser.getId() + " не найден");
        }

        String sql = "SELECT * FROM USERS WHERE EMAIL = ? AND USER_ID != ?";
        if (findOne(sql, updatedUser.getEmail(), updatedUser.getId()).isPresent()) {
            throw new ValidationException("Этот имейл " + updatedUser.getEmail() + " уже используется");
        }
    }

    public void checkUserFieldsOnCreate(User user) {
        log.info("Проверка полей пользователя при его создании; {}", user.getLogin());
        String FIND_BY_EMAIL = "SELECT * FROM USERS WHERE EMAIL =?";
        findOne(FIND_BY_EMAIL, user.getEmail());
        if (findOne(FIND_BY_EMAIL, user.getEmail()).isPresent()) {
            throw new ValidationException("Этот имейл " + user.getEmail() + " уже используется");
        }
        String FIND_BY_LOGIN = "SELECT * FROM USERS WHERE LOGIN = ?";
        if (findOne(FIND_BY_LOGIN, user.getLogin()).isPresent()) {
            throw new ValidationException("Пользователь с таким логином " + user.getLogin() + " уже существует.");
        }
    }
}
