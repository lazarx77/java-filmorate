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
    @Override
    public Event mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return Event.builder()
                .eventId(resultSet.getLong("EVENT_ID"))
                .userId(resultSet.getLong("USER_ID"))
                .timestamp(resultSet.getLong("TIME_ACTION"))
                .EventType(EventTypes.valueOf(resultSet.getString("TYPE")))
                .operation(OperationTypes.valueOf(resultSet.getString("OPERATION")))
                .entityId(resultSet.getLong("ENTITY_ID"))
                .build();
    }
}
