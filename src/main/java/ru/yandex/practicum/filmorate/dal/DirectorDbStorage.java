package ru.yandex.practicum.filmorate.dal;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

@Repository
@Qualifier("DirectorDbStorage")
public class DirectorDbStorage extends BaseRepository<Director> {

    private static final String FIND_ALL_DIRECTORS = "SELECT * FROM DIRECTORS ORDER BY DIRECTOR_ID";
    private static final String FIND_DIRECTOR_BY_ID = "SELECT * FROM DIRECTORS WHERE DIRECTOR_ID = ?";
    private static final String INSERT_DIRECTOR_QUERY = "INSERT INTO DIRECTORS(DIRECTOR_NAME) VALUES (?)";
    private static final String UPDATE_DIRECTOR_QUERY = "UPDATE DIRECTORS SET DIRECTOR_NAME = ? WHERE DIRECTOR_ID = ?";
    private static final String DELETE_DIRECTOR_QUERY = "DELETE FROM DIRECTORS WHERE DIRECTOR_ID = ?";

    public DirectorDbStorage(JdbcTemplate jdbc, RowMapper<Director> mapper) {
        super(jdbc, mapper);
    }

    public List<Director> findAll() {
        return findMany(FIND_ALL_DIRECTORS);
    }

    public Director findById(Long id) {
        return findOne(FIND_DIRECTOR_BY_ID, id)
                .orElseThrow(() -> new RuntimeException("Режиссер с id= " + id + " не найден"));
    }

    public Director createDirector(Director director) {
        long id = insertWithGenId(
                INSERT_DIRECTOR_QUERY,
                director.getName()
        );
        director.setId(id);
        return director;
    }

    public Director update(Director updatedDirector) {
        update(
                UPDATE_DIRECTOR_QUERY,
                updatedDirector.getName()
        );
        return updatedDirector;
    }

    public void delete(Long id) {
        delete(DELETE_DIRECTOR_QUERY, id);
    }
}


