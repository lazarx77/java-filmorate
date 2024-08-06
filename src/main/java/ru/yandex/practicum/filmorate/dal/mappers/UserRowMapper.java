package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Mapper для преобразования строк результата SQL-запроса в объекты User.
 * Реализует интерфейс RowMapper, предоставляя метод для маппинга данных из ResultSet.
 */
@Component
public class UserRowMapper implements RowMapper<User> {

    /**
     * Преобразует строку результата SQL-запроса в объект User.
     *
     * @param resultSet Результат SQL-запроса, содержащий данные о пользователе.
     * @param rowNum Номер строки в результате запроса (начиная с 0).
     * @return Объект User, созданный на основе данных из ResultSet.
     * @throws SQLException Если возникает ошибка при доступе к данным в ResultSet.
     */
    @Override
    public User mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        User user = new User();
        user.setId(resultSet.getLong("USER_ID"));
        user.setName(resultSet.getString("USER_NAME"));
        user.setEmail(resultSet.getString("EMAIL"));
        user.setLogin(resultSet.getString("LOGIN"));
        user.setBirthday(resultSet.getDate("BIRTHDAY").toLocalDate());

        return user;
    }
}
