package ru.yandex.practicum.filmorate.model;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Film.
 * Фильм хранит поля:
 * - id: уникальный идентификатор фильма
 * - name: название фильма, не может быть пустым
 * - description: описание фильма, максимальная длина 200 символов
 * - releaseDate: дата выхода фильма
 * - duration: продолжительность фильма в минутах, не может быть отрицательной
 * - likes: множество идентификаторов пользователей, которым понравился фильм
 */
@Data
@EqualsAndHashCode(of = {"name", "releaseDate"})
public class Film {

    private static final int DESCRIPTION_MAX_LENGTH = 200;
    private Set<Long> likes = new HashSet<>();
    private Long id;
    @NotBlank(message = "Название не может быть пустым")
    private String name;
    @Size(max = DESCRIPTION_MAX_LENGTH, message = "Максимальная длина описания — "
            + DESCRIPTION_MAX_LENGTH + " символов")
    private String description;
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность не может быть отрицательной")
    private Long duration;
    @NotEmpty(message = "Рейтинг не может быть пустым")
    @NotNull(message = "Рейтинг не может быть Null")
    private Rating rating;
    private Set<String> genre = new HashSet<>();
}
