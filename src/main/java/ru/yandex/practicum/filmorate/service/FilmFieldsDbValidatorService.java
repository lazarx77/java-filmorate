package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

/**
 * Сервис для валидации полей фильма в базе данных.
 * <p>
 * Данный класс наследуется от BaseRepository и предоставляет методы
 * для проверки корректности обновляемых данных фильма перед их сохранением
 * в базе данных.
 * </p>
 */
@Slf4j
public class FilmFieldsDbValidatorService extends BaseRepository<Film> {

    private static final String FIND_BY_ID = "SELECT * FROM FILMS WHERE FILM_ID = ?";

    public FilmFieldsDbValidatorService(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    /**
     * Проверяет корректность полей обновляемого фильма.
     * <p>
     * Метод проверяет, существует ли фильм с указанным идентификатором.
     * Если фильм не найден, выбрасывается NotFoundException.
     * Если фильм с таким же именем и датой релиза уже существует,
     * выбрасывается ValidationException.
     * </p>
     *
     * @param updatedFilm Объект Film, содержащий обновленные данные фильма.
     *                    Не должен быть null.
     * @throws NotFoundException   Если фильм с указанным идентификатором не найден.
     * @throws ValidationException Если фильм с таким именем и датой релиза уже существует в базе данных.
     */
    public void validateUpdateFilmFields(Film updatedFilm) {
        if (findOne(FIND_BY_ID, updatedFilm.getId()).isEmpty()) {
            throw new NotFoundException("Фильм с id = " + updatedFilm.getId() + " не найден");
        }
    }
}
