package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * Film.
 */
@Data
@EqualsAndHashCode(of = {"name", "releaseDate"})
public class Film {

    private final int DESCRIPTION_MAX_LENGTH = 200;
    private Long id;
    @NotBlank(message = "Название не может быть пустым")
    private String name;
    @Size(max = DESCRIPTION_MAX_LENGTH, message = "Максимальная длина описания — " + DESCRIPTION_MAX_LENGTH + " символов")
    private String description;
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность не может быть отрицательной")
    private Long duration;
}
