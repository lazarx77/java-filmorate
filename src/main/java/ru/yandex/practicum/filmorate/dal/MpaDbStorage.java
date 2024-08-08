package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

/**
 * Репозиторий для работы с рейтингами фильмов (MPA) в базе данных.
 * <p>
 * Данный класс наследуется от BaseRepository и предоставляет методы
 * для получения информации о рейтингах, включая поиск по идентификатору
 * и получение всех рейтингов.
 * </p>
 */
@Repository
public class MpaDbStorage extends BaseRepository<Mpa> {

    private static final String FIND_BY_ID = "SELECT * FROM MPA WHERE MPA_ID = ?";
    private static final String FIND_ALL_MPA = "SELECT * FROM MPA";

    public MpaDbStorage(JdbcTemplate jdbc, RowMapper<Mpa> mapper) {
        super(jdbc, mapper);
    }

    /**
     * Находит рейтинг по его идентификатору.
     *
     * @param id Идентификатор рейтинга, который необходимо найти.
     * @return Объект Mpa, соответствующий указанному идентификатору.
     * @throws NotFoundException Если рейтинг с указанным идентификатором не найден.
     */
    public Mpa findById(int id) {
        return findOne(FIND_BY_ID, id)
                .orElseThrow(() -> new NotFoundException("MPA " + id + " not found"));
    }

    /**
     * Находит все рейтинги в базе данных.
     *
     * @return Список всех объектов Mpa.
     */
    public List<Mpa> findAll() {
        return findMany(FIND_ALL_MPA);
    }
}
