package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Slf4j
@Repository
public class GenreDbService extends BaseRepository<Genre> {

    private static final String FIND_GENRE_BY_ID = "SELECT * FROM GENRES WHERE GENRE_ID = ?";

    private static final String FIND_GENRES_BY_FILM_ID = "SELECT FILMS_GENRES.GENRE_ID AS GENRE_ID, " +
            "GENRES.GENRE_NAME AS GENRE_NAME FROM FILMS_GENRES LEFT JOIN GENRES ON GENRES.GENRE_ID = " +
            "FILMS_GENRES.GENRE_ID WHERE FILMS_GENRES.FILM_ID = ? ORDER BY FILMS_GENRES.GENRE_ID;";

    public GenreDbService(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    public Genre findById(int id) {
        return findOne(FIND_GENRE_BY_ID, id)
                .orElseThrow(() -> new NotFoundException("Genre " + id + " not found"));
    }

    public List<Genre> findGenresByFilmId(Long filmId) {
        return findMany(FIND_GENRES_BY_FILM_ID, filmId);
    }
    public List<Genre> findAll() {
        return findMany("SELECT * FROM GENRES");
    }

    public String findGenreNameById(int id) {
        return findById(id).getName();
    }

    public void checkGenreId(int id) {
        log.info("Проверка id жанра; {}", id);
        if (findAll().stream().noneMatch(genre -> genre.getId() == id))
            throw new ValidationException("Жанр с id " + id + " не существует");
    }
}
