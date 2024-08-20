package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmDbService;

import java.util.Collection;
import java.util.List;

/**
 * FilmController — это REST-контроллер, который обрабатывает HTTP-запросы, связанные с фильмами:
 * Он предоставляет точки для добавления, обновления и получения фильмов, а также для работы с лайками.
 * Контроллер использует FilmStorage и FilmService для управления данными.
 */
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmDbService filmDbService;

    /**
     * addLike - добавляет лайк фильму с указанным id от пользователя с указанным userId.
     *
     * @param id     идентификатор фильма
     * @param userId идентификатор пользователя
     */
    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable("id") long id, @PathVariable("userId") long userId) {
        filmDbService.addLike(id, userId);
    }

    /**
     * deleteLike - удаляет лайк фильму с указанным id от пользователя с указанным userId.
     *
     * @param id     идентификатор фильма
     * @param userId идентификатор пользователя
     */
    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") long id, @PathVariable("userId") long userId) {
        filmDbService.deleteLike(id, userId);
    }

    /**
     * addFilm - добавляет новый фильм в хранилище. Принимает объект Film в теле запроса.
     *
     * @param film объект Film, представляющий новый фильм
     * @return объект Film, представляющий добавленный фильм
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film addFilm(@Valid @RequestBody Film film) {
        return filmDbService.addFilm(film);
    }

    /**
     * getPopular - возвращает список из count самых популярных фильмов.
     *
     * @param count количество популярных фильмов для возврата
     * @return список из count самых популярных фильмов
     */
    @GetMapping("/popular")
    public List<Film> getPopular(@PathVariable("count") @RequestParam(defaultValue = "10") int count) {
        return filmDbService.getMostLiked(count);
    }

    /**
     * getAll - возвращает коллекцию всех фильмов в хранилище.
     *
     * @return коллекция всех фильмов в хранилище
     */
    @GetMapping
    public Collection<Film> getAll() {
        return filmDbService.getAll();
    }

    /**
     * update - обновляет информацию о фильме в хранилище. Принимает объект Film в теле запроса.
     *
     * @param updatedFilm объект Film, представляющий обновленную информацию о фильме
     * @return объект Film, представляющий обновленный фильм
     */
    @PutMapping
    public Film update(@Valid @RequestBody Film updatedFilm) {
        return filmDbService.update(updatedFilm);
    }

    /**
     * getFilmById - получает фильм с указанным id.
     *
     * @param id идентификатор фильма
     * @return объект Film, представляющий фильм
     */
    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable("id") Long id) {
        return filmDbService.getFilmById(id);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getDirectorFilms(@PathVariable("directorId") Long id, @RequestParam(defaultValue = "year") String sortBy) {
        return filmDbService.getDirectorFilms(id, sortBy);
    }
}
