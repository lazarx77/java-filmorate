package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

/**
 * Сервис для работы с рейтингами фильмов (MPA) в базе данных.
 * <p>
 * Данный класс наследуется от BaseRepository и предоставляет методы
 * для получения информации о рейтингах, включая поиск по идентификатору
 * и получение всех рейтингов.
 * </p>
 */
@Slf4j
@Repository
public class MpaDbService extends BaseRepository<Mpa> {

    private static final String FIND_BY_ID = "SELECT * FROM MPA WHERE MPA_ID = ?";
    private static final String FIND_ALL_MPA = "SELECT * FROM MPA";

    public MpaDbService(JdbcTemplate jdbc, RowMapper<Mpa> mapper) {
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

    /**
     * Находит название рейтинга по его идентификатору.
     *
     * @param id Идентификатор рейтинга, название которого необходимо найти.
     * @return Название рейтинга в виде строки.
     * @throws NotFoundException Если рейтинг с указанным идентификатором не найден.
     */
    public String findMpaNameById(int id) {
        return findById(id).getName();
    }

    /**
     * Проверяет существование рейтинга по его идентификатору.
     *
     * @param id Идентификатор рейтинга, который необходимо проверить.
     * @throws ValidationException Если рейтинг с указанным идентификатором не существует.
     */
    public void checkMpaId(int id) {
        log.info("Проверка id MPA; {}", id);
        if (findOne(FIND_BY_ID, id).isEmpty())
            throw new ValidationException("MPA с id " + id + " не существует");
    }
}
