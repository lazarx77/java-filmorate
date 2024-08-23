package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Маппер для преобразования строк результата SQL-запроса в объекты Review.
 * Реализует интерфейс RowMapper и используется для маппинга данных из базы данных в объекты модели.
 */
@Component
public class ReviewRowMapper implements RowMapper<Review> {

    /**
     * Преобразует строку результата SQL-запроса в объект Review.
     *
     * @param rs     объект ResultSet, содержащий данные из базы данных
     * @param rowNum номер строки, которую необходимо преобразовать
     * @return объект Review, содержащий данные из строки результата
     * @throws SQLException если возникает ошибка при извлечении данных из ResultSet
     */
    @Override
    public Review mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Review.builder()
                .reviewId(rs.getLong("review_id"))
                .content(rs.getString("content"))
                .isPositive(rs.getBoolean("is_positive"))
                .userId(rs.getLong("user_id"))
                .filmId(rs.getLong("film_id"))
                .useful(rs.getInt("useful"))
                .build();
    }
}