package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Mapper для преобразования строк результата SQL-запроса в объекты Genre.
 * Реализует интерфейс RowMapper, предоставляя метод для маппинга данных из ResultSet.
 */
@Component
public class GenreRowMapper implements RowMapper<Genre> {

    /**
     * Преобразует строку результата SQL-запроса в объект Genre.
     *
     * @param resultSet Результат SQL-запроса, содержащий данные о жанре.
     * @param rowNum Номер строки в результате запроса (начиная с 0).
     * @return Объект Genre, созданный на основе данных из ResultSet.
     * @throws SQLException Если возникает ошибка при доступе к данным в ResultSet.
     */
    @Override
    public Genre mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Genre genre = new Genre();
        genre.setId(resultSet.getInt("GENRE_ID"));
        genre.setName(resultSet.getString("GENRE_NAME"));
        return genre;
    }
}
