package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Класс, представляющий режиссера фильма.
 * <p>
 * Данный класс содержит информацию о режиссере, включая его уникальный идентификатор
 * и имя. Имя режиссера должно быть заполнено и не может быть null. Класс использует
 * аннотации для валидации данных, а также аннотации Lombok для автоматической генерации
 * методов доступа и переопределения методов {@code equals} и {@code hashCode} на основе
 * поля {@code name}.
 * </p>
 */
@Data
@EqualsAndHashCode(of = {"name"})
public class Director {
    private Long id;
    @NotEmpty(message = "Имя режиссера должно быть заполнено")
    @NotNull(message = "Имя режиссера не может быть Null")
    private String name;
}
