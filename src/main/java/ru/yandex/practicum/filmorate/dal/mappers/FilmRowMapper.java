package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FilmRowMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getLong("FILM_ID"));
        film.setName(resultSet.getString("FILM_NAME"));
        film.setDescription(resultSet.getString("DESCRIPTION"));

        Rating rating = new Rating();
        rating.setId(resultSet.getInt("RATING_ID"));
        film.setRating(rating);

        film.setReleaseDate(resultSet.getDate("RELEASE_DATE").toLocalDate());
        film.setDuration(resultSet.getLong("DURATION"));
        return film;
    }
}
