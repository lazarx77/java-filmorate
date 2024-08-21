package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

/**
 * Репозиторий для работы с жанрами фильмов в базе данных.
 * <p>
 * Данный класс наследуется от BaseRepository и предоставляет методы
 * для получения информации о жанрах, включая поиск по идентификатору,
 * получение жанров по идентификатору фильма и получение всех жанров.
 * </p>
 */
@Repository
public class GenreDbStorage extends BaseRepository<Genre> {

    private static final String FIND_GENRE_BY_ID = "SELECT * FROM GENRES WHERE GENRE_ID = ?";
    private static final String FIND_GENRES_BY_FILM_ID = "SELECT FILMS_GENRES.GENRE_ID AS GENRE_ID, " +
            "GENRES.GENRE_NAME AS GENRE_NAME FROM FILMS_GENRES LEFT JOIN GENRES ON GENRES.GENRE_ID = " +
            "FILMS_GENRES.GENRE_ID WHERE FILMS_GENRES.FILM_ID = ? ORDER BY FILMS_GENRES.GENRE_ID";
    private static final String FIND_ALL_GENRES = "SELECT * FROM GENRES";

    public GenreDbStorage(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    /**
     * Находит жанр по его идентификатору.
     *
     * @param id Идентификатор жанра, который необходимо найти.
     * @return Объект Genre, соответствующий указанному идентификатору.
     * @throws NotFoundException Если жанр с указанным идентификатором не найден.
     */
    public Genre findById(int id) {
        return findOne(FIND_GENRE_BY_ID, id)
                .orElseThrow(() -> new NotFoundException("Жанр с id " + id + " не найден"));
    }

    /**
     * Находит все жанры, связанные с указанным идентификатором фильма.
     *
     * @param filmId Идентификатор фильма, для которого необходимо найти жанры.
     * @return Список объектов Genre, связанных с указанным фильмом.
     */
    public List<Genre> findGenresByFilmId(Long filmId) {
        return findMany(FIND_GENRES_BY_FILM_ID, filmId);
    }

    /**
     * Находит все жанры в базе данных.
     *
     * @return Список всех объектов Genre.
     */
    public List<Genre> findAll() {
        return findMany(FIND_ALL_GENRES);
    }
}
