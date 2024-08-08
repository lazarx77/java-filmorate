package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaDbService;

import java.util.Collection;

/**
 * Контроллер для работы с рейтингами фильмов (MPA).
 * Предоставляет REST API для получения информации о рейтингах.
 */
@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {
    private final MpaDbService mpaService;

    /**
     * Получает список всех рейтингов фильмов.
     *
     * @return Коллекция объектов Mpa, представляющих все доступные рейтинги.
     */
    @GetMapping
    public Collection<Mpa> getAll() {
        return mpaService.findAll();
    }

    /**
     * Получает рейтинг по его идентификатору.
     *
     * @param id Идентификатор рейтинга, который необходимо получить.
     * @return Объект Mpa, представляющий рейтинг с указанным идентификатором.
     * @throws NotFoundException если рейтинг с указанным идентификатором не найден.
     */
    @GetMapping("/{id}")
    public Mpa getMpaById(@PathVariable("id") int id) {
        return mpaService.findById(id);
    }
}
