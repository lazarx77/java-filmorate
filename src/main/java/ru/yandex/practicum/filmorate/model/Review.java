package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

/**
 * Review.
 * Отзыв хранит поля:
 * - reviewId: уникальный идентификатор отзыва
 * - content: содержимое отзыва, не может быть пустым
 * - isPositive: флаг, указывающий, является ли отзыв положительным, не может быть null
 * - userId: идентификатор пользователя, оставившего отзыв, не может быть null
 * - filmId: идентификатор фильма, к которому относится отзыв, не может быть null
 * - useful: показатель полезности отзыва (может быть рассчитан, например, как разница между лайками и дизлайками)
 */
@Data
@Builder
public class Review {
    private Long reviewId;
    @NotBlank
    private String content;
    @NotNull
    private Boolean isPositive;
    @NotNull
    private Long userId;
    @NotNull
    private Long filmId;
    private Integer useful;
}
