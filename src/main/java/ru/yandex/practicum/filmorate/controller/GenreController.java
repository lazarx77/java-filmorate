package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreDbService;

import java.util.Collection;

/**
 * Контроллер для работы с жанрами фильмов.
 * Предоставляет REST API для получения информации о жанрах.
 */
@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {
    private final GenreDbService genreService;

    /**
     * Получает список всех жанров.
     *
     * @return Коллекция объектов Genre, представляющих все доступные жанры.
     */
    @GetMapping
    public Collection<Genre> getAll() {
        return genreService.findAll();
    }

    /**
     * Получает жанр по его идентификатору.
     *
     * @param id Идентификатор жанра, который необходимо получить.
     * @return Объект Genre, представляющий жанр с указанным идентификатором.
     * @throws NotFoundException Если жанр с указанным идентификатором не найден.
     */
    @GetMapping("/{id}")
    public Genre getGenreById(@PathVariable("id") int id) {
        return genreService.findById(id);
    }
}
