package ru.yandex.practicum.filmorate.model;

import lombok.Data;

/**
 * Представляет жанр фильма.
 * <p>
 * Класс содержит информацию о жанре, включая его уникальный идентификатор
 * и название. Жанры используются для классификации фильмов и могут
 * быть связаны с различными фильмами в системе.
 * </p>
 */
@Data
public class Genre {
    private int id;
    private String name;
}
