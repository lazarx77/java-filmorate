package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.enums.EventTypes;
import ru.yandex.practicum.filmorate.model.enums.OperationTypes;

import java.sql.Timestamp;

@Data
@Builder
public class Event {
    private long eventId;
    private long userId;
    private long timestamp;
    private EventTypes EventType;
    private OperationTypes operation;
    private long entityId;
}
