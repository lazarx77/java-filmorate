package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"name"})
public class Director {
    private Long id;
    @NotEmpty(message = "Имя режиссера должно быть заполнено")
    @NotNull(message = "Имя режиссера не может быть Null")
    private String name;
}
