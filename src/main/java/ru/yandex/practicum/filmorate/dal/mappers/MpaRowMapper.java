package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Mapper для преобразования строк результата SQL-запроса в объекты Mpa.
 * Реализует интерфейс RowMapper, предоставляя метод для маппинга данных из ResultSet.
 */
@Component
public class MpaRowMapper implements RowMapper<Mpa> {

    /**
     * Преобразует строку результата SQL-запроса в объект Mpa.
     *
     * @param resultSet Результат SQL-запроса, содержащий данные о рейтинге.
     * @param rowNum Номер строки в результате запроса (начиная с 0).
     * @return Объект Mpa, созданный на основе данных из ResultSet.
     * @throws SQLException Если возникает ошибка при доступе к данным в ResultSet.
     */
    @Override
    public Mpa mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Mpa mpa = new Mpa();
        mpa.setId(resultSet.getInt("MPA_ID"));
        mpa.setName(resultSet.getString("MPA_NAME"));
        return mpa;
    }
}
