package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

/**
 * Интерфейс для работы с хранилищем фильмов.
 */
public interface FilmStorage {

    /**
     * getAll возвращает коллекцию всех фильмов.
     *
     * @return коллекция всех фильмов
     */
    Collection<Film> getAll();

    /**
     * addFilm добавляет новый фильм в хранилище.
     *
     * @param film фильм для добавления
     * @return добавленный фильм
     */
    Film addFilm(Film film);

    /**
     * update обновляет информацию о фильме в хранилище.
     *
     * @param updatedFilm обновленная информация о фильме
     * @return обновленный фильм
     */
    Film update(Film updatedFilm);

    /**
     * findById находит фильм по его идентификатору.
     *
     * @param id идентификатор фильма
     * @return найденный фильм, если он существует, иначе пустой Optional
     */
    Optional<Film> findById(Long id);
}
