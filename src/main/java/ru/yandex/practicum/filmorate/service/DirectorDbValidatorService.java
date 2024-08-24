package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;

/**
 * Сервис для валидации данных о режиссерах.
 * <p>
 * Данный класс предоставляет методы для проверки корректности данных о режиссерах,
 * таких как имя и идентификатор. Он наследует функциональность от {@link BaseRepository}
 * и использует {@link JdbcTemplate} для выполнения SQL-запросов к базе данных.
 * </p>
 */
@Slf4j
@Service
public class DirectorDbValidatorService extends BaseRepository<Director> {

    private static final String FIND_DIRECTOR_BY_NAME = "SELECT * FROM DIRECTORS WHERE DIRECTOR_NAME =?";
    private static final String FIND_BY_ID = "SELECT * FROM DIRECTORS WHERE DIRECTOR_ID = ?";

    public DirectorDbValidatorService(JdbcTemplate jdbc, RowMapper<Director> mapper) {
        super(jdbc, mapper);
    }

    /**
     * Проверяет корректность поля имени режиссера.
     * <p>
     * Метод проверяет, что имя режиссера не является пустым или null, а также
     * что в базе данных не существует режиссера с таким же именем. Если
     * проверки не проходят, выбрасывается {@link ValidationException}.
     * </p>
     *
     * @param director Объект режиссера, имя которого нужно проверить.
     * @throws ValidationException Если имя режиссера пустое или уже существует в базе данных.
     */
    public void checkDirectorNameField(Director director) {
        log.info("Проверка поля имени режиссера; {}", director.getName());
        if (director.getName() == null || director.getName().isBlank()) {
            throw new ValidationException("Имя режиссера не может быть пустым");
        }
        if (findOne(FIND_DIRECTOR_BY_NAME, director.getName()).isPresent()) {
            throw new ValidationException("Режиссер с таким именем " + director.getName() + " уже существует.");
        }
    }

    /**
     * Проверяет существование режиссера по его идентификатору.
     * <p>
     * Метод проверяет, существует ли режиссер с указанным идентификатором в базе данных.
     * Если режиссер не найден, выбрасывается {@link NotFoundException}.
     * </p>
     *
     * @param id Идентификатор режиссера, который нужно проверить.
     * @throws NotFoundException Если режиссер с указанным идентификатором не существует.
     */
    public void checkDirectorId(Long id) {
        log.info("Проверка id режиссера; {}", id);
        if (findOne(FIND_BY_ID, id).isEmpty())
            throw new NotFoundException("Режиссер с id " + id + " не существует");
    }
}
