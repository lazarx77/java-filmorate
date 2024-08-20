package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.DirectorRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.*;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;

/**
 * Репозиторий для работы с фильмами в базе данных.
 * Реализует интерфейс FilmStorage и предоставляет методы для выполнения операций CRUD с фильмами.
 */
@Slf4j
@Repository
@Qualifier("FilmDbStorage")
public class FilmDbStorage extends BaseRepository<Film> implements FilmStorage {

    // SQL-запросы
    private static final String DELETE_ALL_GENRES_ON_FILM_UPDATE_QUERY = "DELETE FROM FILMS_GENRES WHERE FILM_ID = ?";
    private static final String FIND_ALL_FILMS_QUERY = "SELECT * FROM FILMS";
    private static final String FIND_FILM_BY_ID_QUERY = "SELECT * FROM FILMS WHERE FILM_ID = ?";
    private static final String FIND_LIKES_BY_FILM_ID = "SELECT USER_ID FROM LIKES WHERE FILM_ID = ?";
    private static final String INSERT_FILM_QUERY = "INSERT INTO FILMS(FILM_NAME, RELEASE_DATE, DURATION, " +
            "DESCRIPTION, MPA_ID, DIRECTOR_ID) VALUES (?,?,?,?,?,?)";
    private static final String INSERT_LIKE_QUERY = "INSERT INTO LIKES(FILM_ID, USER_ID) VALUES (?,?)";
    private static final String INSERT_FILM_GENRE_QUERY = "INSERT INTO FILMS_GENRES(FILM_ID, GENRE_ID) VALUES (?,?)";

    private static final String UPDATE_QUERY = "UPDATE FILMS SET FILM_NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, " +
            "DURATION = ?, MPA_ID = ?, DIRECTOR_ID = ? WHERE FILM_ID = ?";
    private static final String DELETE_LIKE_QUERY = "DELETE FROM LIKES WHERE FILM_ID = ? AND USER_ID = ?";
    private static final String COUNT_LIKES_QUERY = "SELECT COUNT(*) FROM LIKES WHERE FILM_ID =? AND USER_ID =?";
    private static final String FIND_FILMS_OF_DIRECTOR = "SELECT * FROM FILMS WHERE DIRECTOR_ID = ?";

    private final RowMapper<Mpa> mpaMapper = new MpaRowMapper();
    private final RowMapper<Genre> genreMapper = new GenreRowMapper();
    private final RowMapper<Director> directorMapper = new DirectorRowMapper();

    private final MpaDbService mpaDbService = new MpaDbService(new MpaDbStorage(jdbc, mpaMapper));
    private final GenreFieldsDbValidator genreDbValidator = new GenreFieldsDbValidator(jdbc, genreMapper);
    private final GenreDbService genreDbService = new GenreDbService(new GenreDbStorage(jdbc, genreMapper));

    private final DirectorDbValidatorService directorDbValidatorService = new DirectorDbValidatorService(jdbc, directorMapper);

    private final DirectorDbService directorDbService = new DirectorDbService(new DirectorDbStorage(jdbc,
            directorMapper), directorDbValidatorService);

    /**
     * Конструктор для инициализации FilmDbStorage.
     *
     * @param jdbc   JdbcTemplate для выполнения SQL-запросов.
     * @param mapper RowMapper для преобразования строк результата SQL-запроса в объекты Film.
     */
    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    private final FilmFieldsDbValidatorService filmDbValidator = new FilmFieldsDbValidatorService(jdbc, mapper);

    /**
     * Получает все фильмы из базы данных.
     *
     * @return Коллекция всех фильмов.
     */
    @Override
    public Collection<Film> getAll() {
        List<Film> films = findMany(FIND_ALL_FILMS_QUERY);
        for (Film film : films) {
            film.setLikes(new HashSet<>(findManyInstances(FIND_LIKES_BY_FILM_ID, Long.class, film.getId())));
            film.setMpa(mpaDbService.findById(film.getMpa().getId()));
            if (directorDbService.findById(film.getDirector().getId()) != null) {
                film.setDirector(directorDbService.findById(film.getDirector().getId()));
            }
            film.setGenres(new HashSet<>(genreDbService.findGenresByFilmId(film.getId())));
            System.out.println(film);

        }
        return films;
    }

    /**
     * Добавляет новый фильм в базу данных.
     *
     * @param film Фильм, который нужно добавить.
     * @return Добавленный фильм с установленным идентификатором.
     */
    @Override
    public Film addFilm(Film film) {
        long id = insertWithGenId(INSERT_FILM_QUERY, film.getName(), film.getReleaseDate(), film.getDuration(),
                film.getDescription(), film.getMpa().getId(), film.getDirector().getId());
        Set<Genre> genres = film.getGenres();
        if (genres != null) {
            for (Genre genre : genres) {
                genreDbValidator.checkGenreId(genre.getId());
            }
            for (Genre genre : genres) {
                genre.setName(genreDbService.findGenreNameById(genre.getId()));
                insert(INSERT_FILM_GENRE_QUERY, id, genre.getId());
            }
        }
        film.setId(id);
        film.setLikes(new HashSet<>(findManyInstances(FIND_LIKES_BY_FILM_ID, Long.class, id)));
        film.getMpa().setName(mpaDbService.findMpaNameById(film.getMpa().getId()));
        if (directorDbService.findById(film.getDirector().getId()) != null) {
            film.getDirector().setName(directorDbService.findDirectorNameById(film.getDirector().getId()));
        }

        return film;
    }

    /**
     * Обновляет существующий фильм в базе данных.
     *
     * @param updatedFilm Фильм с обновленными данными.
     * @return Обновленный фильм.
     */
    @Override
    public Film update(Film updatedFilm) {
        Set<Genre> genres = updatedFilm.getGenres();
        if (genres != null) {
            for (Genre genre : genres) {
                genreDbValidator.checkGenreId(genre.getId());
            }
            delete(DELETE_ALL_GENRES_ON_FILM_UPDATE_QUERY, updatedFilm.getId());
            for (Genre genre : genres) {
                genre.setName(genreDbService.findGenreNameById(genre.getId()));
                insert(INSERT_FILM_GENRE_QUERY, updatedFilm.getId(), genre.getId());
            }
        }

        filmDbValidator.validateUpdateFilmFields(updatedFilm);
        directorDbValidatorService.checkDirectorId(updatedFilm.getDirector().getId());

        update(
                UPDATE_QUERY, updatedFilm.getName(), updatedFilm.getDescription(), updatedFilm.getReleaseDate(),
                updatedFilm.getDuration(), updatedFilm.getMpa().getId(), updatedFilm.getDirector().getId(),
                updatedFilm.getId()
        );
        log.info("Данные фильма с названием: {} обновлены.", updatedFilm.getName());
        return getFilmById(updatedFilm.getId());
    }

    /**
     * Находит фильм по его идентификатору.
     *
     * @param id Идентификатор фильма.
     * @return Optional фильма, если найден, иначе - пустой Optional.
     */
    @Override
    public Optional<Film> findById(Long id) {
        return findOne(FIND_FILM_BY_ID_QUERY, id);
    }

    /**
     * Получает фильм по его идентификатору.
     *
     * @param id Идентификатор фильма.
     * @return Фильм с указанным идентификатором.
     * @throws NotFoundException Если фильм не найден.
     */
    public Film getFilmById(Long id) {
        Film film = findById(id).orElseThrow(() -> new NotFoundException("Фильм с id " + id + " не найден"));
        Set<Genre> genres = film.getGenres();
        for (Genre genre : genres) {
            genre.setName(genreDbService.findGenreNameById(genre.getId()));
        }
        film.setLikes(new HashSet<>(findManyInstances(FIND_LIKES_BY_FILM_ID, Long.class, id)));
        film.getMpa().setName(mpaDbService.findMpaNameById(film.getMpa().getId()));
        if (directorDbService.findById(film.getDirector().getId()) != null) {
            film.getDirector().setName(directorDbService.findDirectorNameById(film.getDirector().getId()));
        }
        film.setGenres(new HashSet<>(genreDbService.findGenresByFilmId(id)));
        return film;
    }

    /**
     * Добавляет лайк к фильму от пользователя.
     *
     * @param filmId Идентификатор фильма, к которому добавляется лайк.
     * @param userId Идентификатор пользователя, который ставит лайк.
     * @throws NotFoundException Если фильм или пользователь не найдены.
     */
    public void addLike(Long filmId, Long userId) {
        insert(INSERT_LIKE_QUERY, filmId, userId);
    }

    /**
     * Удаляет лайк пользователя от фильма.
     *
     * @param filmId Идентификатор фильма, у которого удаляется лайк.
     * @param userId Идентификатор пользователя, который удаляет лайк.
     * @throws NotFoundException Если фильма нет лайка от пользователя.
     */
    public void deleteLike(Long filmId, Long userId) {
        if (findManyInstances(COUNT_LIKES_QUERY, Long.class, filmId, userId).getFirst() == 0) {
            throw new NotFoundException("У фильма с id " + filmId + " нет лайка от пользователя с id " + userId);
        }
        deleteByTwoIds(DELETE_LIKE_QUERY, filmId, userId);
    }
}
