package ru.yandex.practicum.filmorate.model;

import lombok.Data;

/**
 * Представляет рейтинг фильма (MPA - Motion Picture Association).
 * <p>
 * Класс содержит информацию о рейтинге, включая его уникальный идентификатор
 * и название. Рейтинги используются для классификации фильмов по возрастным
 * категориям и помогают зрителям определить, подходит ли фильм для
 * просмотра.
 * </p>
 */
@Data
public class Mpa {
    private int id;
    private String name;
}
