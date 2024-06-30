package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FieldsValidator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * FilmController.
 */
@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private static final Map<Long, Film> films = new HashMap<>();

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        // проверяем выполнение необходимых условий
        log.info("Проверка даты выпуска фильма при добавлении в картотеку: {}.", film.getName());
        FieldsValidator.validateReleaseDate(film);

        // формируем дополнительные данные
        film.setId(getNextId());
        // сохраняем новый фильм в памяти приложения
        films.put(film.getId(), film);
        log.info("Пользователь добавил фильм в картотеку: {}, дата выпуска - {}.", film.getName(),
                film.getReleaseDate());
        return films.get(film.getId());
    }

    @GetMapping
    public Collection<Film> getAll() {
        return films.values();
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film updatedFilm) {
        // проверяем необходимые условия
        log.info("Проверка налиячия Id у фильма при обновлении: {}.", updatedFilm.getName());
        FieldsValidator.validateFilmId(updatedFilm);

        log.info("Проверка даты выпуска фильма при обновлении: {}.", updatedFilm.getName());
        FieldsValidator.validateReleaseDate(updatedFilm);

        log.info("Проверка полей фильма при обновлении: {}.", updatedFilm.getName());
        FieldsValidator.validateUpdateFilmFields(updatedFilm, films);

        films.put(updatedFilm.getId(), updatedFilm);
        log.info("Пользователь обновил данные по фильму в картотеке: {}, дата выпуска - {}.",
                updatedFilm.getName(), updatedFilm.getReleaseDate());

        return films.get(updatedFilm.getId());
    }

    // вспомогательный метод для генерации идентификатора нового фильма
    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
