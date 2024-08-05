package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.MpaRowMapper;
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

//import static ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage.films;

@Slf4j
@Repository
@Qualifier("FilmDbStorage")
public class FilmDbStorage extends BaseRepository<Film> implements FilmStorage {

    private static final String DELETE_ALL_GENRES_ON_FILM_UPDATE_QUERY = "DELETE FROM FILMS_GENRES WHERE FILM_ID = ?";
    private static final String FIND_ALL_FILMS_QUERY = "SELECT * FROM FILMS";
    private static final String FIND_FILM_BY_ID_QUERY = "SELECT * FROM FILMS WHERE FILM_ID = ?";
    private static final String FIND_LIKES_BY_FILM_ID = "SELECT USER_ID FROM LIKES WHERE FILM_ID = ?";
    private static final String FIND_MPA_BY_MPA_ID = "SELECT MPA_NAME FROM MPA WHERE MPA_ID = ?";
    private static final String FIND_GENRES_BY_FILM_ID = "SELECT FILMS_GENRES.GENRE_ID, GENRES.GENRE_NAME \n" +
            "FROM FILMS_GENRES \n" +
            "LEFT JOIN GENRES ON GENRES.GENRE_ID = FILMS_GENRES.GENRE_ID \n" +
            "WHERE FILMS_GENRES.FILM_ID = ?;";
    private static final String INSERT_FILM_QUERY = "INSERT INTO FILMS(FILM_NAME, RELEASE_DATE, DURATION, " +
            "DESCRIPTION, MPA_ID) VALUES (?,?,?,?,?)";
    private static final String INSERT_FILM_GENRE_QUERY = "INSERT INTO FILMS_GENRES(FILM_ID, GENRE_ID) VALUES (?,?)";
    private static final String FIND_MPA = "SELECT MPA FROM FILMS WHERE FILM_ID = ?";

    private static final String UPDATE_QUERY = "UPDATE FILMS SET FILM_NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, " +
            "DURATION = ?, MPA_ID = ? WHERE FILM_ID = ?";
    private final RowMapper<Mpa> mpaMapper = new MpaRowMapper();
    private final RowMapper<Genre> genreMapper = new GenreRowMapper();
    private final MpaDbService mpaDbService = new MpaDbService(jdbc, mpaMapper);
    private final GenreDbService genreDbService = new GenreDbService(jdbc, genreMapper);

    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
//        this.mpaMapper = mpaMapper;
    }

    private final FilmFieldsDbValidatorService filmDbValidator = new FilmFieldsDbValidatorService(jdbc, mapper);

    @Override
    public Collection<Film> getAll() {
        List<Film> films = findMany(FIND_ALL_FILMS_QUERY);
        for (Film film : films) {
            System.out.println(film);
            film.setLikes(new HashSet<>(findManyInstances(FIND_LIKES_BY_FILM_ID, Long.class, film.getId())));
            film.setMpa(mpaDbService.findById(film.getMpa().getId()));
            film.setGenres(new HashSet<>(findManyInstances(FIND_GENRES_BY_FILM_ID, Genre.class, film.getId())));
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
            insert(INSERT_FILM_GENRE_QUERY, id, genre.getId());
        }
        film.setLikes(new HashSet<>(findManyInstances(FIND_LIKES_BY_FILM_ID, Long.class, id)));
        film.getMpa().setName(mpaDbService.findMpaNameById(film.getMpa().getId()));
        return film;
    }
}
