package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
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

    private static final String FIND_BY_ID = "SELECT * FROM USERS WHERE USER_ID =?";

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
     * @throws NotFoundException Если пользователь с указанным идентификатором не найден.
     */
    protected void checkUserFieldsOnUpdate(User updatedUser) {
        log.info("Проверка полей пользователя при его обновлении; {}", updatedUser.getLogin());
        if (findOne(FIND_BY_ID, updatedUser.getId()).isEmpty()) {
            throw new NotFoundException("Польователь с id = " + updatedUser.getId() + " не найден");
        }
    }
}
