package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.enums.EventTypes;
import ru.yandex.practicum.filmorate.model.enums.OperationTypes;

/**
 * Event.
 * Событие хранит поля:
 * - eventId: уникальный идентификатор действия
 * - userId: идетнификатор пользователя, который совершил действие
 * - timestamp: время действия
 * - eventTypes: тип действия
 * - operation: операция действия
 * - entityId: идентификатор сущности, над которой совершено действие
 */
@Data
@Builder
public class Event {
    @NotNull
    private long eventId;
    @NotNull
    private long userId;
    @NotNull
    private long timestamp;
    @NotNull
    private EventTypes eventTypes;
    @NotNull
    private OperationTypes operation;
    @NotNull
    private long entityId;
}
