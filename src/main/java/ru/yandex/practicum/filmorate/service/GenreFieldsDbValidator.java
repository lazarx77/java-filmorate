package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Genre;

/**
 * Сервис для валидации полей жанров в базе данных.
 * Проверяет корректность идентификаторов жанров и обеспечивает
 * целостность данных при работе с жанрами.
 */
@Slf4j
@Service
public class GenreFieldsDbValidator extends BaseRepository<Genre> {

    private static final String FIND_ALL_GENRES = "SELECT * FROM GENRES";

    public GenreFieldsDbValidator(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    /**
     * Проверяет существование жанра по его идентификатору.
     *
     * @param id Идентификатор жанра, который необходимо проверить.
     * @throws ValidationException Если жанр с указанным идентификатором не существует.
     */
    public void checkGenreId(int id) {
        log.info("Проверка id жанра; {}", id);
        if (findMany(FIND_ALL_GENRES).stream().noneMatch(genre -> genre.getId() == id))
            throw new ValidationException("Жанр с id " + id + " не существует");
    }
}
