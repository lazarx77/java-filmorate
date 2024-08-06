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

@Slf4j
@Repository
public class MpaDbService extends BaseRepository<Mpa> {

    private static final String FIND_BY_ID = "SELECT * FROM MPA WHERE MPA_ID = ?";
    private static final String FIND_ALL_MPA = "SELECT * FROM MPA";

    public MpaDbService(JdbcTemplate jdbc, RowMapper<Mpa> mapper) {
        super(jdbc, mapper);
    }

    public Mpa findById(int id) {
        return findOne(FIND_BY_ID, id)
                .orElseThrow(() -> new NotFoundException("MPA " + id + " not found"));
    }

    public List<Mpa> findAll() {
        return findMany(FIND_ALL_MPA);
    }

    public String findMpaNameById(int id) {
        return findById(id).getName();
    }

    public void checkMpaId(int id) {
        log.info("Проверка id MPA; {}", id);
        if (findOne(FIND_BY_ID, id).isEmpty())
            throw new ValidationException("MPA с id " + id + " не существует");
    }
}
