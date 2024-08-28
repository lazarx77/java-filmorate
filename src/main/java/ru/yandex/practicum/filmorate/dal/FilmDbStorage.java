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
import ru.yandex.practicum.filmorate.service.DirectorDbService;
import ru.yandex.practicum.filmorate.service.DirectorDbValidatorService;
import ru.yandex.practicum.filmorate.service.GenreDbService;
import ru.yandex.practicum.filmorate.service.GenreFieldsDbValidator;
import ru.yandex.practicum.filmorate.service.MpaDbService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
    private static final String DELETE_ALL_DIRECTORS_ON_FILM_UPDATE_QUERY = "DELETE FROM FILMS_DIRECTORS" +
            " WHERE FILM_ID = ?";
    private static final String FIND_ALL_FILMS_QUERY = "SELECT * FROM FILMS";
    private static final String FIND_FILM_BY_ID_QUERY = "SELECT * FROM FILMS WHERE FILM_ID = ?";
    private static final String FIND_LIKES_BY_FILM_ID = "SELECT USER_ID FROM RATINGS WHERE FILM_ID = ?";
    private static final String UPDATE_FILM_RATING_QUERY = """
            UPDATE FILMS SET FILM_RATING = (SELECT ROUND(AVG(USER_RATING),1) FROM RATINGS WHERE FILM_ID = ?)
            WHERE FILM_ID = ?
            """;
    private static final String INSERT_FILM_QUERY = "INSERT INTO FILMS(FILM_NAME, RELEASE_DATE, DURATION, " +
            "DESCRIPTION, MPA_ID, FILM_RATING) VALUES (?,?,?,?,?,?)";
    private static final String INSERT_LIKE_QUERY = "INSERT INTO RATINGS(FILM_ID, USER_ID, USER_RATING) VALUES (?,?,?)";
    private static final String INSERT_FILM_GENRE_QUERY = "INSERT INTO FILMS_GENRES(FILM_ID, GENRE_ID) VALUES (?,?)";
    private static final String INSERT_FILM_DIRECTOR_QUERY = "INSERT INTO FILMS_DIRECTORS(FILM_ID, DIRECTOR_ID)" +
            " VALUES (?,?)";
    private static final String UPDATE_QUERY = "UPDATE FILMS SET FILM_NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, " +
            "DURATION = ?, MPA_ID = ? WHERE FILM_ID = ?";
    private static final String DELETE_LIKE_QUERY = "DELETE FROM RATINGS WHERE FILM_ID = ? AND USER_ID = ?";
    private static final String COUNT_LIKES_QUERY = "SELECT COUNT(*) FROM RATINGS WHERE FILM_ID =? AND USER_ID =?";
    private static final String COMMON_FILMS_QUERY = """
            WITH USER_films AS (
                SELECT f.film_id, count(*) AS cnt
                FROM RATINGS l
                INNER JOIN films f ON f.film_id = l.film_id
                WHERE l.user_id = ?
                GROUP BY f.film_id
            ),
            friend_films AS (
                SELECT f.film_id, count(*) AS cnt
                FROM RATINGS l
                INNER JOIN films f ON f.film_id = l.film_id
                WHERE l.user_id = ?
                GROUP BY f.film_id
            )
            SELECT f.*
            FROM films f
            INNER JOIN USER_films u ON f.FILM_ID = u.film_id
            INNER JOIN friend_films ff ON ff.film_id = f.FILM_ID
            ORDER BY u.cnt DESC
            """;
    private static final String DELETE_FILM_QUERY = "DELETE FROM FILMS WHERE FILM_ID = ?";
    private static final String DELETE_FILM_GENRE_QUERY = "DELETE FROM FILMS_GENRES WHERE FILM_ID = ?";
    private static final String DELETE_FILM_LIKE_QUERY = "DELETE FROM RATINGS WHERE FILM_ID = ?";
    private static final String DELETE_FILM_REVIEW_QUERY = "DELETE FROM REVIEWS WHERE FILM_ID = ?";
    private static final String GET_USER_LIKES_QUERY = """
            WITH prep AS (
                SELECT l1.USER_ID, COUNT(*) AS cnt
                FROM RATINGS l1
                INNER JOIN RATINGS l2 ON l2.FILM_ID = l1.FILM_ID
                                      AND l2.USER_ID = ?
                                      AND ABS(l1.USER_RATING - l2.USER_RATING) <= 1
                WHERE l1.USER_ID != ?
                GROUP BY l1.USER_ID
                ORDER BY COUNT(*) DESC
            )
            SELECT f.*
            FROM RATINGS l1
            INNER JOIN prep p ON p.USER_ID = l1.USER_ID
            LEFT JOIN RATINGS l2 ON l2.FILM_ID = l1.FILM_ID
                                 AND l2.USER_ID = ?
            INNER JOIN films f ON f.FILM_ID = l1.FILM_ID
            WHERE l2.FILM_ID IS NULL
                  AND f.FILM_RATING >= 6
            ORDER BY p.cnt DESC
            """;
    private final RowMapper<Mpa> mpaMapper = new MpaRowMapper();
    private final RowMapper<Genre> genreMapper = new GenreRowMapper();
    private final RowMapper<Director> directorMapper = new DirectorRowMapper();

    private final MpaDbService mpaDbService = new MpaDbService(new MpaDbStorage(jdbc, mpaMapper));
    private final GenreFieldsDbValidator genreDbValidator = new GenreFieldsDbValidator(jdbc, genreMapper);
    private final GenreDbService genreDbService = new GenreDbService(new GenreDbStorage(jdbc, genreMapper));

    private final DirectorDbValidatorService directorDbValidatorService =
            new DirectorDbValidatorService(jdbc, directorMapper);

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

    /**
     * Получает все фильмы из базы данных.
     *
     * @return Коллекция всех фильмов.
     */
    @Override
    public Collection<Film> getAll() {
        List<Film> films = findMany(FIND_ALL_FILMS_QUERY);
        for (Film film : films) {
            film.setUsersRatedFilm(new HashSet<>(findManyInstances(FIND_LIKES_BY_FILM_ID, Long.class, film.getId())));
            film.setMpa(mpaDbService.findById(film.getMpa().getId()));
            film.setGenres(new HashSet<>(genreDbService.findGenresByFilmId(film.getId())));
            List<Director> directors = directorDbService.findDirectorsByFilmId(film.getId());
            for (Director director : directors) {
                director.setName(directorDbService.findDirectorNameById(director.getId()));
            }
            film.setDirectors(new HashSet<>(directors));
        }
        return films;
    }

    public void updateUserRating(Long filmId) {
        log.info("Updating rating Film ID = {}", filmId);
        update(UPDATE_FILM_RATING_QUERY, filmId, filmId);
        log.info("Updated rating Film ID = {}", filmId);
    }

    /**
     * Добавляет новый фильм в базу данных.
     *
     * @param film Фильм, который нужно добавить.
     * @return Добавленный фильм с установленным идентификатором.
     */
    @Override
    public Film addFilm(Film film) {
        film.setFilmRating(0.0);
        long id = insertWithGenId(INSERT_FILM_QUERY, film.getName(), film.getReleaseDate(), film.getDuration(),
                film.getDescription(), film.getMpa().getId(), film.getFilmRating());
        Set<Genre> genres = film.getGenres();
        Set<Genre> sortedGenres = new HashSet<>();
        if (genres != null && !genres.isEmpty()) {
            for (Genre genre : genres) {
                genreDbValidator.checkGenreId(genre.getId());
            }
            sortedGenres = genres.stream()
                    .sorted(Comparator.comparing(Genre::getId))
                    .collect(Collectors.toCollection(LinkedHashSet::new));

            for (Genre genre : sortedGenres) {
                genre.setName(genreDbService.findGenreNameById(genre.getId()));
                insert(INSERT_FILM_GENRE_QUERY, id, genre.getId());
            }
        }
        Set<Director> directors = film.getDirectors();
        if (directors != null && !directors.isEmpty()) {
            for (Director director : directors) {
                directorDbValidatorService.checkDirectorId(director.getId());
            }

            for (Director director : directors) {
                director.setName(directorDbService.findDirectorNameById(director.getId()));
                insert(INSERT_FILM_DIRECTOR_QUERY, id, director.getId());
            }
        }
        film.getMpa().setName(mpaDbService.findMpaNameById(film.getMpa().getId()));
        film.setId(id);
        film.setUsersRatedFilm(new HashSet<>(findManyInstances(FIND_LIKES_BY_FILM_ID, Long.class, id)));
        film.setGenres(sortedGenres);
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
            Set<Genre> sortedGenres = Set.copyOf(genres.stream().sorted(Comparator.comparing(Genre::getId))
                    .collect(Collectors.toList()));
            for (Genre genre : sortedGenres) {
                genre.setName(genreDbService.findGenreNameById(genre.getId()));
                insert(INSERT_FILM_GENRE_QUERY, updatedFilm.getId(), genre.getId());
            }
        }
        Set<Director> director = updatedFilm.getDirectors();
        if (director != null) {
            for (Director d : director) {
                directorDbValidatorService.checkDirectorId(d.getId());
            }
            delete(DELETE_ALL_DIRECTORS_ON_FILM_UPDATE_QUERY, updatedFilm.getId());
            for (Director d : director) {
                d.setName(directorDbService.findDirectorNameById(d.getId()));
                insert(INSERT_FILM_DIRECTOR_QUERY, updatedFilm.getId(), d.getId());
            }
        } else {
            delete(DELETE_ALL_DIRECTORS_ON_FILM_UPDATE_QUERY, updatedFilm.getId());
        }

        update(
                UPDATE_QUERY, updatedFilm.getName(), updatedFilm.getDescription(), updatedFilm.getReleaseDate(),
                updatedFilm.getDuration(), updatedFilm.getMpa().getId(), updatedFilm.getId()
        );
        updatedFilm.setUsersRatedFilm(new HashSet<>(findManyInstances(FIND_LIKES_BY_FILM_ID,
                Long.class,
                updatedFilm.getId())));
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
        film.getMpa().setName(mpaDbService.findMpaNameById(film.getMpa().getId()));
        Set<Director> directors = new HashSet<>(directorDbService.findDirectorsByFilmId(id));
        for (Director director : directors) {
            director.setName(directorDbService.findDirectorNameById(director.getId()));
        }
        film.setDirectors(directors);
        film.setGenres(new HashSet<>(genreDbService.findGenresByFilmId(id)));
        film.setUsersRatedFilm(new HashSet<>(findManyInstances(FIND_LIKES_BY_FILM_ID, Long.class, id)));
        return film;
    }

    /**
     * Добавляет лайк к фильму от пользователя.
     *
     * @param filmId Идентификатор фильма, к которому добавляется лайк.
     * @param userId Идентификатор пользователя, который ставит лайк.
     * @throws NotFoundException Если фильм или пользователь не найдены.
     */
    public void addRating(Long filmId, Long userId, Integer userRating) {
        insert(INSERT_LIKE_QUERY, filmId, userId, userRating);
        updateUserRating(filmId);
    }

    /**
     * Удаляет лайк пользователя от фильма.
     *
     * @param filmId Идентификатор фильма, у которого удаляется лайк.
     * @param userId Идентификатор пользователя, который удаляет лайк.
     * @throws NotFoundException Если фильма нет лайка от пользователя.
     */
    public void deleteRating(Long filmId, Long userId) {
        if (findManyInstances(COUNT_LIKES_QUERY, Long.class, filmId, userId).getFirst() == 0) {
            throw new NotFoundException("У фильма с id " + filmId + " нет лайка от пользователя с id " + userId);
        }
        deleteByTwoIds(DELETE_LIKE_QUERY, filmId, userId);
        updateUserRating(filmId);
    }

    public List<Film> getCommonFilms(long userId, long friendId) {
        List<Film> result = super.findMany(COMMON_FILMS_QUERY, userId, friendId);
        for (Film film : result) {
            film.setGenres(new HashSet<>(genreDbService.findGenresByFilmId(film.getId())));
            film.getMpa().setName(mpaDbService.findMpaNameById(film.getMpa().getId()));
        }
        return result;
    }

    public void deleteFilm(long filmId) {
        delete(DELETE_FILM_REVIEW_QUERY, filmId);
        delete(DELETE_FILM_LIKE_QUERY, filmId);
        delete(DELETE_FILM_GENRE_QUERY, filmId);
        delete(DELETE_FILM_QUERY, filmId);
    }

    public List<Film> getRecommendations(long id) {
        List<Film> films = findMany(GET_USER_LIKES_QUERY, id, id, id);
        for (Film film : films) {
            film.setUsersRatedFilm(new HashSet<>(findManyInstances(FIND_LIKES_BY_FILM_ID, Long.class, id)));
            film.setMpa(mpaDbService.findById(film.getMpa().getId()));
            film.setGenres(new HashSet<>(genreDbService.findGenresByFilmId(film.getId())));
            List<Director> directors = directorDbService.findDirectorsByFilmId(film.getId());
            for (Director director : directors) {
                director.setName(directorDbService.findDirectorNameById(director.getId()));
            }
            film.setDirectors(new HashSet<>(directors));
        }
        return films;
    }
}
