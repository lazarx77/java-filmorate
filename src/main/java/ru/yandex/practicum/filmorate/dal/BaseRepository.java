package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import ru.yandex.practicum.filmorate.exceptions.InternalServerException;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

/**
 * Базовый репозиторий для работы с базой данных.
 * Предоставляет общие методы для выполнения операций CRUD (создание, чтение, обновление, удаление).
 *
 * @param <T> Тип сущности, с которой работает репозиторий.
 */
@RequiredArgsConstructor
public class BaseRepository<T> {

    protected final JdbcTemplate jdbc;
    protected final RowMapper<T> mapper;

    /**
     * Находит одну сущность по заданному SQL-запросу.
     *
     * @param query  SQL-запрос для поиска сущности.
     * @param params Параметры для SQL-запроса.
     * @return Опциональная сущность типа T, если найдена, иначе пустой Optional.
     */
    protected Optional<T> findOne(String query, Object... params) {
        try {
            T result = jdbc.queryForObject(query, mapper, params);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    /**
     * Находит одно значение типа {@code Double} в базе данных, выполняя заданный SQL-запрос.
     *
     * <p>Метод использует {@link JdbcTemplate} для выполнения запроса и возвращает результат в виде
     * {@link Optional}. Если запрос не возвращает ни одной строки, метод возвращает {@link Optional#empty()}.
     *
     * <p>Пример использования:
     * <pre>
     * Optional<Double> value = findOneEntity("SELECT price FROM products WHERE id = ?", productId);
     * value.ifPresent(price -> System.out.println("Цена продукта: " + price));
     * </pre>
     *
     * @param query  SQL-запрос, который будет выполнен. Запрос должен возвращать одно значение типа {@code Double}.
     * @param params Параметры, которые будут подставлены в запрос. Параметры должны соответствовать
     *               местам в запросе, обозначенным знаками вопроса (?).
     * @return {@link Optional} содержащий найденное значение типа {@code Double}, или {@link Optional#empty()}
     * если значение не найдено.
     * @throws DataAccessException если возникает ошибка доступа к данным при выполнении запроса.
     *
     *                             <p>Метод обрабатывает {@link EmptyResultDataAccessException}, которая выбрасывается, если запрос не
     *                             возвращает ни одной строки, и возвращает пустой {@link Optional} в этом случае.
     *
     *                             <p>Важно отметить, что метод возвращает только одно значение. Если запрос возвращает более одной строки,
     *                             это приведет к исключению. Для получения нескольких значений следует использовать другие методы,
     *                             такие как {@code queryForList} или {@code query}.
     */
    protected Optional<Double> findOneEntity(String query, Object... params) {
        try {
            Double result = jdbc.queryForObject(query, new SingleColumnRowMapper<>(Double.class), params);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    /**
     * Находит одно значение одного столбца по заданному SQL-запросу.
     *
     * @param query  SQL-запрос для поиска значений.
     * @param params Параметры для SQL-запроса.
     * @return Одно значение типа String.
     */
    protected Optional<String> findOneInstances(String query, Object... params) {
        try {
            String result = jdbc.queryForObject(query, new SingleColumnRowMapper<>(String.class), params);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    /**
     * Находит множество сущностей по заданному SQL-запросу.
     *
     * @param query  SQL-запрос для поиска сущностей.
     * @param params Параметры для SQL-запроса.
     * @return Список сущностей типа T.
     */
    protected List<T> findMany(String query, Object... params) {
        return jdbc.query(query, mapper, params);
    }

    /**
     * Находит множество значений одного столбца по заданному SQL-запросу.
     *
     * @param query  SQL-запрос для поиска значений.
     * @param type   Тип значений, которые нужно вернуть.
     * @param params Параметры для SQL-запроса.
     * @param <T>    Тип значений, которые нужно вернуть.
     * @return Список значений типа T.
     */
    protected <T> List<T> findManyInstances(String query, Class<T> type, Object... params) {
        return jdbc.query(query, new SingleColumnRowMapper<>(type), params);
    }

    /**
     * Удаляет сущность по заданному SQL-запросу и идентификатору.
     *
     * @param query SQL-запрос для удаления сущности.
     * @param id    Идентификатор сущности, которую нужно удалить.
     * @return true, если сущность была успешно удалена, иначе false.
     */
    protected boolean delete(String query, long id) {
        int rowsDeleted = jdbc.update(query, id);
        return rowsDeleted > 0;
    }

    /**
     * Удаляет сущность по двум идентификаторам.
     *
     * @param query SQL-запрос для удаления сущности.
     * @param id    Первый идентификатор сущности.
     * @param id2   Второй идентификатор сущности.
     * @return true, если сущность была успешно удалена, иначе false.
     */
    protected boolean deleteByTwoIds(String query, long id, long id2) {
        int rowsDeleted = jdbc.update(query, id, id2);
        return rowsDeleted > 0;
    }

    /**
     * Удаляет сущность по двум идентификаторам и значению.
     *
     * @param query SQL-запрос для удаления сущности.
     * @param id    Первый идентификатор сущности.
     * @param id2   Второй идентификатор сущности.
     * @param like  Значение сущности.
     * @return true, если сущность была успешно удалена, иначе false.
     */
    protected boolean deleteByTwoIdsAndLike(String query, long id, long id2, String like) {
        int rowsDeleted = jdbc.update(query, id, id2, like);
        return rowsDeleted > 0;
    }

    /**
     * Вставляет новую сущность в базу данных и возвращает сгенерированный идентификатор.
     *
     * @param query  SQL-запрос для вставки сущности.
     * @param params Параметры для SQL-запроса.
     * @return Сгенерированный идентификатор новой сущности.
     * @throws InternalServerException Если не удалось сохранить данные.
     */
    protected long insertWithGenId(String query, Object... params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            for (int idx = 0; idx < params.length; idx++) {
                ps.setObject(idx + 1, params[idx]);
            }
            return ps;
        }, keyHolder);

        Long id = keyHolder.getKeyAs(Long.class);
        if (id != null) {
            return id;
        } else {
            throw new InternalServerException("Не удалось сохранить данные");
        }
    }

    /**
     * Вставляет новую сущность в базу данных без возврата сгенерированного идентификатора.
     *
     * @param query  SQL-запрос для вставки сущности.
     * @param params Параметры для SQL-запроса.
     */
    protected void insert(String query, Object... params) {

        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(query, Statement.NO_GENERATED_KEYS);
            for (int idx = 0; idx < params.length; idx++) {
                ps.setObject(idx + 1, params[idx]);
            }
            return ps;
        });
    }

    /**
     * Обновляет существующую сущность в базе данных.
     *
     * @param query  SQL-запрос для обновления сущности.
     * @param params Параметры для SQL-запроса.
     * @throws InternalServerException Если не удалось обновить данные.
     */
    protected void update(String query, Object... params) {
        int rowsUpdated = jdbc.update(query, params);
        if (rowsUpdated == 0) {
            throw new InternalServerException("Не удалось обновить данные");
        }
    }
}
