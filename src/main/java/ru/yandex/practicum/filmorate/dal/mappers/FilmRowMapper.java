package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Mapper для преобразования строк результата SQL-запроса в объекты Film.
 * Реализует интерфейс RowMapper, предоставляя метод для маппинга данных из ResultSet .
 */
@Component
public class FilmRowMapper implements RowMapper<Film> {

    /**
     * Преобразует строку результата SQL-запроса в объект Film.
     *
     * @param resultSet Результат SQL-запроса, содержащий данные о фильме.
     * @param rowNum    Номер строки в результате запроса (начиная с 0).
     * @return Объект Film, созданный на основе данных из ResultSet.
     * @throws SQLException Если возникает ошибка при доступе к данным в ResultSet.
     */
    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getLong("FILM_ID"));
        film.setName(resultSet.getString("FILM_NAME"));
        film.setDescription(resultSet.getString("DESCRIPTION"));
        film.setReleaseDate(resultSet.getDate("RELEASE_DATE").toLocalDate());
        film.setDuration(resultSet.getLong("DURATION"));

        Mpa mpa = new Mpa();
        mpa.setId(resultSet.getInt("MPA_ID"));
        film.setMpa(mpa);

        return film;
    }
}
