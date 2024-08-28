package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmDbService;

import java.util.Collection;
import java.util.List;

/**
 * FilmController — это REST-контроллер, который обрабатывает HTTP-запросы, связанные с фильмами:
 * Он предоставляет точки для добавления, обновления и получения фильмов, а также для работы с лайками.
 * Контроллер использует FilmDbStorage для управления данными.
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
    public void addRating(@PathVariable("id") long id,
                          @PathVariable("userId") long userId,
                          @RequestParam Integer userRating) {
        filmDbService.addRating(id, userId, userRating);
    }

    /**
     * deleteLike - удаляет лайк фильму с указанным id от пользователя с указанным userId.
     *
     * @param id     идентификатор фильма
     * @param userId идентификатор пользователя
     */
    @DeleteMapping("/{id}/like/{userId}")
    public void deleteRating(@PathVariable("id") long id, @PathVariable("userId") long userId) {
        filmDbService.deleteRating(id, userId);
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
     * @param count   количество популярных фильмов для возврата
     * @param genreId указывает жанр фильмов для возврата
     * @param year    указывает год фильмов для возврата
     * @return список из count самых популярных фильмов
     */
    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(required = false) Integer count,
                                      @RequestParam(required = false) Integer genreId,
                                      @RequestParam(required = false) Integer year) {
        return filmDbService.getPopularFilms(count, genreId, year);
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

    @GetMapping("/common")
    public List<Film> getCommonFilms(@RequestParam long userId, @RequestParam long friendId) {
        return filmDbService.getCommonFilms(userId, friendId);
    }

    /**
     * getDirectorFilms - возвращает список фильмов одного режиссера, отсортированный по указанному параметру.
     *
     * @param id     идентификатор режиссера
     * @param sortBy параметр сортировки (по умолчанию "year")
     * @return список фильмов одного режиссера, отсортированный по указанному параметру
     */
    @GetMapping("/director/{directorId}")
    public List<Film> getDirectorFilms(@PathVariable("directorId") Long id,
                                       @RequestParam(defaultValue = "year") String sortBy) {
        return filmDbService.getDirectorFilms(id, sortBy);
    }

    /**
     * getFilmById - удаляет фильм.
     *
     * @param id идентификатор фильма
     */
    @DeleteMapping("/{id}")
    public void deleteFilm(@PathVariable("id") long id) {
        filmDbService.deleteFilm(id);
    }


    /**
     * searchFilm - поиск фильмов по названию и режиссеру.
     *
     * @param query значаение для поиска
     * @param by    поиск выполнять по названию фильма, режиссера или вместе
     * @return результат поиска
     */
    @GetMapping("/search")
    public List<Film> searchFilm(@RequestParam("query") String query,
                                 @RequestParam("by") String by) {
        return filmDbService.searchFilms(query, by);
    }
}
