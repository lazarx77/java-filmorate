package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.enums.EventTypes;
import ru.yandex.practicum.filmorate.model.enums.OperationTypes;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class EventRowMapper implements RowMapper<Event> {
    /**
     * Преобразует строку результата SQL-запроса в объект Film.
     *
     * @param resultSet Результат SQL-запроса, содержащий данные о фильме.
     * @param rowNum    Номер строки в результате запроса (начиная с 0).
     * @return Объект Event, созданный на основе данных из ResultSet.
     * @throws SQLException Если возникает ошибка при доступе к данным в ResultSet.
     */
    @Override
    public Event mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return Event.builder()
                .eventId(resultSet.getLong("EVENT_ID"))
                .userId(resultSet.getLong("USER_ID"))
                .timestamp(resultSet.getLong("TIME_ACTION"))
                .eventType(EventTypes.valueOf(resultSet.getString("TYPE")))
                .operation(OperationTypes.valueOf(resultSet.getString("OPERATION")))
                .entityId(resultSet.getLong("ENTITY_ID"))
                .build();
    }
}
