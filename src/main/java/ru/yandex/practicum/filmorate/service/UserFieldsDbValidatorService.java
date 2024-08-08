package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;


/**
 * Сервис для валидации полей пользователя в базе данных.
 * <p>
 * Данный класс наследуется от BaseRepository и предоставляет методы
 * для проверки корректности данных пользователя при его создании и обновлении.
 * </p>
 */
@Slf4j
@Service
public class UserFieldsDbValidatorService extends BaseRepository<User> {

    private static final String FIND_BY_EMAIL = "SELECT * FROM USERS WHERE EMAIL =?";
    private static final String FIND_BY_LOGIN = "SELECT * FROM USERS WHERE LOGIN =?";
    private static final String FIND_BY_ID = "SELECT * FROM USERS WHERE USER_ID =?";
    private static final String FIND_BY_EMAIL_AND_USER_ID = "SELECT * FROM USERS WHERE EMAIL = ? AND USER_ID != ?";

    public UserFieldsDbValidatorService(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    /**
     * Проверяет корректность полей пользователя при его обновлении.
     * <p>
     * Метод проверяет, существует ли пользователь с указанным идентификатором.
     * Если пользователь не найден, выбрасывается NotFoundException.
     * Если указанный email уже используется другим пользователем, выбрасывается
     * ValidationException.
     * </p>
     *
     * @param updatedUser Объект User, содержащий обновленные данные пользователя.
     *                    Не должен быть null.
     * @throws NotFoundException   Если пользователь с указанным идентификатором не найден.
     * @throws ValidationException Если email уже используется другим пользователем.
     */
    protected void checkUserFieldsOnUpdate(User updatedUser) {
        log.info("Проверка полей пользователя при его обновлении; {}", updatedUser.getLogin());
        if (findOne(FIND_BY_ID, updatedUser.getId()).isEmpty()) {
            throw new NotFoundException("Польователь с id = " + updatedUser.getId() + " не найден");
        }
        if (findOne(FIND_BY_EMAIL_AND_USER_ID, updatedUser.getEmail(), updatedUser.getId()).isPresent()) {
            throw new ValidationException("Этот имейл " + updatedUser.getEmail() + " уже используется");
        }
    }

    /**
     * Проверяет корректность полей пользователя при его создании.
     * <p>
     * Метод проверяет, существует ли пользователь с указанным email или логином.
     * Если email уже используется, выбрасывается ValidationException.
     * Если логин уже используется, выбрасывается ValidationException.
     * </p>
     *
     * @param user Объект User, содержащий данные нового пользователя.
     *             Не должен быть null.
     * @throws ValidationException Если email или логин уже используются другими пользователями.
     */
    protected void checkUserFieldsOnCreate(User user) {
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
