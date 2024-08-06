package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

@Slf4j
public class UserFieldsDbValidatorService extends BaseRepository<User> {

    private static final String FIND_BY_EMAIL = "SELECT * FROM USERS WHERE EMAIL =?";
    private static final String FIND_BY_LOGIN = "SELECT * FROM USERS WHERE LOGIN =?";
    private static final String FIND_BY_ID = "SELECT * FROM USERS WHERE USER_ID =?";
    private static final String FIND_BY_EMAIL_AND_USER_ID = "SELECT * FROM USERS WHERE EMAIL = ? AND USER_ID != ?";

    public UserFieldsDbValidatorService(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    public void checkUserFieldsOnUpdate(User updatedUser) {
        log.info("Проверка полей пользователя при его обновлении; {}", updatedUser.getLogin());
        if (findOne(FIND_BY_ID, updatedUser.getId()).isEmpty()) {
            throw new NotFoundException("Польователь с id = " + updatedUser.getId() + " не найден");
        }
        if (findOne(FIND_BY_EMAIL_AND_USER_ID, updatedUser.getEmail(), updatedUser.getId()).isPresent()) {
            throw new ValidationException("Этот имейл " + updatedUser.getEmail() + " уже используется");
        }
    }

    public void checkUserFieldsOnCreate(User user) {
        log.info("Проверка полей пользователя при его создании; {}", user.getLogin());
        findOne(FIND_BY_EMAIL, user.getEmail());
        if (findOne(FIND_BY_EMAIL, user.getEmail()).isPresent()) {
            throw new ValidationException("Этот имейл " + user.getEmail() + " уже используется");
        }
        if (findOne(FIND_BY_LOGIN, user.getLogin()).isPresent()) {
            throw new ValidationException("Пользователь с таким логином " + user.getLogin() + " уже существует.");
        }
    }
}
