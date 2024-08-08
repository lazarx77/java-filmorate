package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.MpaDbStorage;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

/**
 * Сервис для работы с рейтингами фильмов (MPA) в базе данных.
 * Предоставляет методы для получения информации о рейтингах,
 * включая поиск по идентификатору и получение всех рейтингов.
 */
@Service
public class MpaDbService {

    private final MpaDbStorage mpaDbStorage;

    public MpaDbService(MpaDbStorage mpaDbStorage) {
        this.mpaDbStorage = mpaDbStorage;
    }

    /**
     * Находит рейтинг по его идентификатору.
     *
     * @param id Идентификатор рейтинга.
     * @return Рейтинг с указанным идентификатором.
     * @throws NotFoundException Если рейтинг с указанным идентификатором не найден.
     */
    public Mpa findById(int id) {
        return mpaDbStorage.findById(id);
    }

    /**
     * Возвращает список всех рейтингов.
     *
     * @return Список всех рейтингов в базе данных.
     */
    public List<Mpa> findAll() {
        return mpaDbStorage.findAll();
    }

    /**
     * Находит название рейтинга по его идентификатору.
     *
     * @param id Идентификатор рейтинга.
     * @return Название рейтинга с указанным идентификатором.
     * @throws NotFoundException Если рейтинг с указанным идентификатором не найден.
     */
    public String findMpaNameById(int id) {
        return mpaDbStorage.findById(id).getName();
    }
}
