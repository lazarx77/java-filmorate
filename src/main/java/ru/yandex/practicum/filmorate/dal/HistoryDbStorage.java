package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;

import java.util.Collection;

/**
 * Класс для работы с историей действий пользователей в базе данных.
 * Позволяет добавлять события и получать историю событий для конкретного пользователя.
 */
@Slf4j
@Repository
public class HistoryDbStorage extends BaseRepository<Event> {
    // SQL-запросы
    private static final String INSERT_QUERY = "INSERT INTO HISTORY_ACTIONS(USER_ID, TIME_ACTION, TYPE, OPERATION," +
            " ENTITY_ID)" +
            " VALUES (?,?,?,?,?)";
    private static final String FIND_EVENT_BY_USER_ID_QUERY = "SELECT * FROM HISTORY_ACTIONS WHERE USER_ID = ?";

    public HistoryDbStorage(JdbcTemplate jdbc, RowMapper<Event> mapper) {
        super(jdbc, mapper);
    }

    /**
     * Добавляет событие.
     *
     * @param event Событие.
     * @return Событие с выставленным уникальным id.
     */
    public Event addEvent(Event event) {
        long id = insertWithGenId(
                INSERT_QUERY,
                event.getUserId(),
                event.getTimestamp(),
                event.getEventType().toString(),
                event.getOperation().toString(),
                event.getEntityId()
        );
        event.setEventId(id);
        log.info("Создан ЭВЕНТ c ID {}. Добавлено действие {} {} = {} пользователя c ID = {}", event.getEventId(), event.getEventType().toString(),
                event.getOperation().toString(), event.getEntityId(), event.getUserId());
        return event;
    }

    /**
     * Находит действия по id пользователя.
     *
     * @param userId Идентификатор пользователя, для которого ищутся действия.
     * @return Колекция Event, состоящая из событий пользователя.
     */
    public Collection<Event> getEventsByUser(long userId) {
        return findMany(
                FIND_EVENT_BY_USER_ID_QUERY,
                userId
        );
    }
}
