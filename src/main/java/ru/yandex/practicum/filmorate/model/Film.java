package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Film.
 */
@Data
@EqualsAndHashCode(of = {"name", "releaseDate"})
public class Film {

    private static final int DESCRIPTION_MAX_LENGTH = 200;
    private Set<Long> likes = new HashSet<>();
    private Long id;
    @NotBlank(message = "Название не может быть пустым")
    private String name;
    @Size(max = DESCRIPTION_MAX_LENGTH, message = "Максимальная длина описания — " + DESCRIPTION_MAX_LENGTH + " символов")
    private String description;
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность не может быть отрицательной")
    private Long duration;
}
