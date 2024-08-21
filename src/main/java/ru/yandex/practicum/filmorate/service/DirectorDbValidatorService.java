package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;

@Slf4j
@Service
public class DirectorDbValidatorService extends BaseRepository<Director> {

    private static final String FIND_DIRECTOR_BY_NAME = "SELECT * FROM DIRECTORS WHERE DIRECTOR_NAME =?";
    private static final String FIND_BY_ID = "SELECT * FROM DIRECTORS WHERE DIRECTOR_ID = ?";

    public DirectorDbValidatorService(JdbcTemplate jdbc, RowMapper<Director> mapper) {
        super(jdbc, mapper);
    }

    public void checkDirectorNameField(Director director) {
        log.info("Проверка поля имени режиссера; {}", director.getName());
        if (director.getName() == null || director.getName().isBlank()) {
            throw new ValidationException("Имя режиссера не может быть пустым");
        }
        if (findOne(FIND_DIRECTOR_BY_NAME, director.getName()).isPresent()) {
            throw new ValidationException("Режиссер с таким именем " + director.getName() + " уже существует.");
        }
    }

    public void checkDirectorId(Long id) {
        log.info("Проверка id режиссера; {}", id);
        if (findOne(FIND_BY_ID, id).isEmpty())
            throw new NotFoundException("Режиссер с id " + id + " не существует");
    }


}
