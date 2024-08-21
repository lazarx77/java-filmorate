package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Маппер для преобразования строк результата SQL-запроса в объекты {@link Director}.
 * <p>
 * Этот класс реализует интерфейс {@link RowMapper} и используется для маппинга данных
 * из базы данных в объекты модели. Он позволяет извлекать информацию о режиссерах
 * из результата SQL-запроса и создавать соответствующие объекты {@link Director}.
 * <p>
 * Аннотация {@link Component} указывает, что этот класс является компонентом Spring и может
 * быть автоматически обнаружен и внедрен в другие компоненты.
 */
@Component
public class DirectorRowMapper implements RowMapper<Director> {

    /**
     * Преобразует текущую строку результата SQL-запроса в объект {@link Director}.
     * <p>
     * Этот метод вызывается для каждой строки результата запроса. Он извлекает значения
     * из объекта {@link ResultSet} и создает новый объект {@link Director}, заполняя его
     * соответствующими данными.
     *
     * @param resultSet Объект {@link ResultSet}, содержащий данные из результата SQL-запроса.
     * @param rowNum    Номер текущей строки в результате запроса (начиная с 0).
     * @return Объект {@link Director}, созданный на основе данных текущей строки результата.
     * @throws SQLException Если возникает ошибка при доступе к данным в {@link ResultSet}.
     */
    @Override
    public Director mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Director director = new Director();
        director.setId(resultSet.getLong("DIRECTOR_ID"));
        director.setName(resultSet.getString("DIRECTOR_NAME"));
        return director;
    }
}
