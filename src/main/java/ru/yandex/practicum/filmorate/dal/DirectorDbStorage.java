package ru.yandex.practicum.filmorate.dal;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;


/**
 * Репозиторий для управления данными о режиссерах в базе данных.
 * <p>
 * Этот класс отвечает за выполнение операций CRUD (создание, чтение, обновление, удаление)
 * для сущности "Режиссер". Он использует {@link JdbcTemplate} для выполнения SQL-запросов
 * и маппинга результатов в объекты {@link Director}.
 * <p>
 * Аннотация {@link Repository} указывает, что этот класс является компонентом доступа к данным,
 * а аннотация {@link Qualifier} задает имя бина для внедрения зависимостей.
 */
@Repository
@Qualifier("DirectorDbStorage")
public class DirectorDbStorage extends BaseRepository<Director> {

    //SQL запросы для работы с базой данных
    private static final String FIND_ALL_DIRECTORS = "SELECT * FROM DIRECTORS ORDER BY DIRECTOR_ID";
    private static final String FIND_DIRECTOR_BY_ID = "SELECT * FROM DIRECTORS WHERE DIRECTOR_ID = ?";
    private static final String INSERT_DIRECTOR_QUERY = "INSERT INTO DIRECTORS(DIRECTOR_NAME) VALUES (?)";
    private static final String UPDATE_DIRECTOR_QUERY = "UPDATE DIRECTORS SET DIRECTOR_NAME = ? WHERE DIRECTOR_ID = ?";
    private static final String DELETE_DIRECTOR_QUERY = "DELETE FROM DIRECTORS WHERE DIRECTOR_ID = ?";
    private static final String FIND_DIRECTORS_BY_FILM_ID = "SELECT FILMS_DIRECTORS.DIRECTOR_ID AS DIRECTOR_ID, " +
            "DIRECTORS.DIRECTOR_NAME AS DIRECTOR_NAME " +
            "FROM FILMS_DIRECTORS " +
            "LEFT JOIN DIRECTORS ON DIRECTORS.DIRECTOR_ID = FILMS_DIRECTORS.DIRECTOR_ID " +
            "WHERE FILMS_DIRECTORS.FILM_ID = ? " +
            "ORDER BY FILMS_DIRECTORS.DIRECTOR_ID";

    public DirectorDbStorage(JdbcTemplate jdbc, RowMapper<Director> mapper) {
        super(jdbc, mapper);
    }

    /**
     * Получает список всех режиссеров из базы данных.
     * <p>
     * Этот метод выполняет SQL-запрос для получения всех записей о режиссерах
     * и возвращает их в виде списка.
     *
     * @return Список объектов {@link Director}, представляющих всех режиссеров.
     */
    public List<Director> findAll() {
        return findMany(FIND_ALL_DIRECTORS);
    }

    /**
     * Находит режиссера по его идентификатору.
     * <p>
     * Этот метод выполняет SQL-запрос для поиска режиссера с указанным идентификатором.
     * Если режиссер не найден, выбрасывается исключение {@link NotFoundException}.
     *
     * @param id Идентификатор режиссера, который необходимо найти.
     * @return Объект {@link Director} с указанным идентификатором.
     * @throws NotFoundException Если режиссер с указанным идентификатором не найден.
     */
    public Director findById(Long id) {
        return findOne(FIND_DIRECTOR_BY_ID, id)
                .orElseThrow(() -> new NotFoundException("Режиссер с id= " + id + " не найден"));
    }

    /**
     * Создает нового режиссера в базе данных.
     * <p>
     * Этот метод выполняет SQL-запрос для вставки нового режиссера и возвращает
     * созданный объект {@link Director} с присвоенным идентификатором.
     *
     * @param director Объект {@link Director}, содержащий информацию о новом режиссере.
     * @return Созданный объект {@link Director} с присвоенным идентификатором.
     */
    public Director createDirector(Director director) {
        long id = insertWithGenId(
                INSERT_DIRECTOR_QUERY,
                director.getName()
        );
        director.setId(id);
        return director;
    }

    /**
     * Обновляет информацию о существующем режиссере.
     * <p>
     * Этот метод выполняет SQL-запрос для обновления данных о режиссере
     * на основе переданного объекта {@link Director}.
     *
     * @param updatedDirector Объект {@link Director} с обновленной информацией.
     * @return Объект {@link Director} с обновленной информацией.
     */
    public Director update(Director updatedDirector) {
        update(
                UPDATE_DIRECTOR_QUERY,
                updatedDirector.getName(), updatedDirector.getId()
        );
        return updatedDirector;
    }

    /**
     * Удаляет режиссера из базы данных по его идентификатору.
     * <p>
     * Этот метод выполняет SQL-запрос для удаления режиссера с указанным идентификатором.
     *
     * @param id Идентификатор режиссера, которого необходимо удалить.
     */
    public void delete(Long id) {
        delete(DELETE_DIRECTOR_QUERY, id);
    }

    /**
     * Получает список режиссеров, связанных с указанным фильмом.
     * <p>
     * Этот метод выполняет SQL-запрос для получения всех режиссеров,
     * связанных с фильмом по его идентификатору.
     *
     * @param filmId Идентификатор фильма, для которого необходимо получить режиссеров.
     * @return Список объектов {@link Director}, связанных с указанным фильмом.
     */
    public List<Director> findDirectorsByFilmId(Long filmId) {
        return findMany(FIND_DIRECTORS_BY_FILM_ID, filmId);
    }
}
