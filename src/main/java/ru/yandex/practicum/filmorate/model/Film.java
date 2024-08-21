package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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
 * - mpa: объект типа Mpa, который содержит информацию о рейтинге фильма
 * - genres: множество жанров, к которым относится фильм
 */
@Data
@EqualsAndHashCode(of = {"name", "releaseDate"})
public class Film {

    private static final int DESCRIPTION_MAX_LENGTH = 200;
    private Set<Long> likes = new HashSet<>();
    private Long id;
    @NotBlank(message = "Название не может быть пустым")
    private String name;
    private LocalDate releaseDate;
    @Size(max = DESCRIPTION_MAX_LENGTH, message = "Максимальная длина описания — "
            + DESCRIPTION_MAX_LENGTH + " символов")
    private String description;
    @Positive(message = "Продолжительность не может быть отрицательной")
    private Long duration;
    @NotNull(message = "Рейтинг не может быть Null")
    private Mpa mpa;
    @NotNull(message = "Поле ЖАНРЫ не может быть Null")
    private Set<Genre> genres = new HashSet<>();
    private Set<Director> directors;
}
