package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FieldsValidatorService;
import ru.yandex.practicum.filmorate.service.FilmFieldsDbValidatorService;
import ru.yandex.practicum.filmorate.service.GenreDbService;
import ru.yandex.practicum.filmorate.service.MpaDbService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Repository
@Qualifier("FilmDbStorage")
public class FilmDbStorage extends BaseRepository<Film> implements FilmStorage {

    private static final String DELETE_ALL_GENRES_ON_FILM_UPDATE_QUERY = "DELETE FROM FILMS_GENRES WHERE FILM_ID = ?";
    private static final String FIND_ALL_FILMS_QUERY = "SELECT * FROM FILMS";
    private static final String FIND_FILM_BY_ID_QUERY = "SELECT * FROM FILMS WHERE FILM_ID = ?";
    private static final String FIND_LIKES_BY_FILM_ID = "SELECT USER_ID FROM LIKES WHERE FILM_ID = ?";
    private static final String INSERT_FILM_QUERY = "INSERT INTO FILMS(FILM_NAME, RELEASE_DATE, DURATION, " +
            "DESCRIPTION, MPA_ID) VALUES (?,?,?,?,?)";
    private static final String INSERT_LIKE_QUERY = "INSERT INTO LIKES(FILM_ID, USER_ID) VALUES (?,?)";
    private static final String INSERT_FILM_GENRE_QUERY = "INSERT INTO FILMS_GENRES(FILM_ID, GENRE_ID) VALUES (?,?)";

    private static final String UPDATE_QUERY = "UPDATE FILMS SET FILM_NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, " +
            "DURATION = ?, MPA_ID = ? WHERE FILM_ID = ?";
    private static final String DELETE_LIKE_QUERY = "DELETE FROM LIKES WHERE FILM_ID = ? AND USER_ID = ?";
    private static final String COUNT_LIKES_QUERY = "SELECT COUNT(*) FROM LIKES WHERE FILM_ID =? AND USER_ID =?";
    private final RowMapper<Mpa> mpaMapper = new MpaRowMapper();
    private final RowMapper<Genre> genreMapper = new GenreRowMapper();
    private final MpaDbService mpaDbService = new MpaDbService(jdbc, mpaMapper);
    private final GenreDbService genreDbService = new GenreDbService(jdbc, genreMapper);

    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    private final FilmFieldsDbValidatorService filmDbValidator = new FilmFieldsDbValidatorService(jdbc, mapper);
    private final UserDbStorage userDbStorage = new UserDbStorage(jdbc, new UserRowMapper());

    @Override
    public Collection<Film> getAll() {
        List<Film> films = findMany(FIND_ALL_FILMS_QUERY);
        for (Film film : films) {
            film.setLikes(new HashSet<>(findManyInstances(FIND_LIKES_BY_FILM_ID, Long.class, film.getId())));
            film.setMpa(mpaDbService.findById(film.getMpa().getId()));
            film.setGenres(new HashSet<>(genreDbService.findGenresByFilmId(film.getId())));
            System.out.println(film);

        }
        return films;
    }

    @Override
    public Film addFilm(Film film) {
        FieldsValidatorService.validateReleaseDate(film);
        mpaDbService.checkMpaId(film.getMpa().getId());
        long id = insertWithGenId(INSERT_FILM_QUERY, film.getName(), film.getReleaseDate(), film.getDuration(),
                film.getDescription(), film.getMpa().getId());
        Set<Genre> genres = film.getGenres();
        if (genres != null) {
            for (Genre genre : genres) {
                genreDbService.checkGenreId(genre.getId());
            }
            for (Genre genre : genres) {
                genre.setName(genreDbService.findGenreNameById(genre.getId()));
                insert(INSERT_FILM_GENRE_QUERY, id, genre.getId());
            }
        }
        film.setId(id);
        film.setLikes(new HashSet<>(findManyInstances(FIND_LIKES_BY_FILM_ID, Long.class, id)));
        film.getMpa().setName(mpaDbService.findMpaNameById(film.getMpa().getId()));
        log.info("Фильм {} добавлен", film.getName());
        return film;
    }

    @Override
    public Film update(Film updatedFilm) {
        log.info("Проверка налиячия Id у фильма при обновлении: {}.", updatedFilm.getName());
        FieldsValidatorService.validateFilmId(updatedFilm);
        log.info("Проверка даты выпуска фильма при обновлении: {}.", updatedFilm.getName());
        FieldsValidatorService.validateReleaseDate(updatedFilm);
        log.info("Проверка полей фильма при обновлении: {}.", updatedFilm.getName());
        mpaDbService.checkMpaId(updatedFilm.getMpa().getId());
        Set<Genre> genres = updatedFilm.getGenres();
        if (genres != null) {
            for (Genre genre : genres) {
                genreDbService.checkGenreId(genre.getId());
            }
            delete(DELETE_ALL_GENRES_ON_FILM_UPDATE_QUERY, updatedFilm.getId());
            for (Genre genre : genres) {
                genre.setName(genreDbService.findGenreNameById(genre.getId()));
                insert(INSERT_FILM_GENRE_QUERY, updatedFilm.getId(), genre.getId());
            }
        }
        filmDbValidator.validateUpdateFilmFields(updatedFilm);

        update(
                UPDATE_QUERY, updatedFilm.getName(), updatedFilm.getDescription(), updatedFilm.getReleaseDate(),
                updatedFilm.getDuration(), updatedFilm.getMpa().getId(), updatedFilm.getId()
        );
        log.info("Данные фильма с названием: {} обновлены.", updatedFilm.getName());
        return getFilmById(updatedFilm.getId());
    }

    @Override
    public Optional<Film> findById(Long id) {
        return findOne(FIND_FILM_BY_ID_QUERY, id);
    }

    public Film getFilmById(Long id) {
        Film film = findById(id).orElseThrow(() -> new NotFoundException("Фильм с id " + id + " не найден"));
        Set<Genre> genres = film.getGenres();
        for (Genre genre : genres) {
            genre.setName(genreDbService.findGenreNameById(genre.getId()));
        }
        film.setLikes(new HashSet<>(findManyInstances(FIND_LIKES_BY_FILM_ID, Long.class, id)));
        film.getMpa().setName(mpaDbService.findMpaNameById(film.getMpa().getId()));
        film.setGenres(new HashSet<>(genreDbService.findGenresByFilmId(id)));
        return film;
    }

    public void addLike(Long filmId, Long userId) {
        userDbStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        findById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id " + filmId + " не найден"));
        insert(INSERT_LIKE_QUERY, filmId, userId);
        log.info("Фильму с id {} добавлен like пользователя с id {}.", filmId, userId);
    }

    /**
     * deleteLike - удаляет лайк пользователя с идентификаторов userId у фильма с идентификатором filmId.
     *
     * @param filmId идентификатор фильма, у которого удаляется лайк.
     * @param userId идентификатор пользователя, который удаляет лайк.
     * @throws NotFoundException если фильм не найден или у фильма нет лайка от пользователя.
     */
    public void deleteLike(Long filmId, Long userId) {
        log.info("Проверка существования фильма и пользователя: {} и {}", filmId, userId);
        findById(filmId).orElseThrow(() -> new NotFoundException("Фильм с id " + filmId + " не найден"));
        userDbStorage.findById(userId);
        if (findManyInstances(COUNT_LIKES_QUERY, Long.class, filmId, userId).getFirst() == 0) {
            throw new NotFoundException("У фильма с id " + filmId + " нет лайка от пользователя с id " + userId);
        }
        deleteByTwoIds(DELETE_LIKE_QUERY, filmId, userId);
        log.info("У фильма с id {} удален like пользователя id {}.", filmId, userId);
    }

    /**
     * getMostLiked - возвращает список из count самых популярных фильмов.
     *
     * @param count количество фильмов, которые нужно вернуть.
     * @return список из count самых популярных фильмов.
     */
    public List<Film> getMostLiked(int count) {
        Comparator<Film> comparator = Comparator.comparing(film -> film.getLikes().size(), Comparator.reverseOrder());
        return getAll()
                .stream()
                .sorted(comparator)
                .limit(count)
                .collect(Collectors.toList());
    }
}
