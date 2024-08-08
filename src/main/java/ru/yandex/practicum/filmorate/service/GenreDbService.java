package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.GenreDbStorage;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

/**
 * Сервис для работы с жанрами фильмов в базе данных.
 * Предоставляет методы для получения информации о жанрах,
 * включая поиск по идентификатору и получение всех жанров.
 */
@Service
@AllArgsConstructor
public class GenreDbService {

    private final GenreDbStorage genreDbStorage;

    /**
     * Находит жанр по его идентификатору.
     *
     * @param id Идентификатор жанра.
     * @return Жанр с указанным идентификатором.
     * @throws NotFoundException Если жанр с указанным идентификатором не найден.
     */
    public Genre findById(int id) {
        return genreDbStorage.findById(id);
    }

    /**
     * Находит список жанров, связанных с фильмом по его идентификатору.
     *
     * @param filmId Идентификатор фильма, для которого нужно получить жанры.
     * @return Список жанров, связанных с указанным фильмом.
     */
    public List<Genre> findGenresByFilmId(Long filmId) {
        return genreDbStorage.findGenresByFilmId(filmId);
    }

    /**
     * Находит название жанра по его идентификатору.
     *
     * @param id Идентификатор жанра.
     * @return Название жанра с указанным идентификатором.
     * @throws NotFoundException Если жанр с указанным идентификатором не найден.
     */
    public String findGenreNameById(int id) {
        return findById(id).getName();
    }

    /**
     * Возвращает список всех жанров.
     *
     * @return Список всех жанров в базе данных.
     */
    public List<Genre> findAll() {
        return genreDbStorage.findAll();
    }
}
