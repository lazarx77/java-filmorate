package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
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

    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);

    Map<Long, Film> films = new HashMap<>();

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        // проверяем выполнение необходимых условий
        if (film.getReleaseDate().isBefore(CINEMA_BIRTHDAY)) {
            throw new ValidationException("Дата релиза не может быть раньше дня рождения Кино");
        }

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
    public Film update(@RequestBody Film updatedFilm) {
        // проверяем необходимые условия
        if (updatedFilm.getId() == null) {
            throw new ValidationException("Id должен быть указан");
        }
        if (films.containsKey(updatedFilm.getId())) {
            Film oldFilm = films.get(updatedFilm.getId());
            films.remove(updatedFilm.getId());

            for (Long id : films.keySet()) {
                Film middleFilm = films.get(id);
                if (updatedFilm.getName().equals(middleFilm.getName()) && updatedFilm.getReleaseDate().equals(middleFilm.getReleaseDate())) {
                    films.put(updatedFilm.getId(), oldFilm);
                    throw new ValidationException("Этот фильм уже есть картотеке");
                }
            }
            if (updatedFilm.getName() == null) {
                updatedFilm.setName(oldFilm.getName());
            }
            if (updatedFilm.getReleaseDate() == null) {
                updatedFilm.setReleaseDate(oldFilm.getReleaseDate());
            }
            if (updatedFilm.getDescription() == null) {
                updatedFilm.setDuration(oldFilm.getDuration());
            }
            if (updatedFilm.getDuration() == null) {
                updatedFilm.setDuration(oldFilm.getDuration());
            }
            films.put(updatedFilm.getId(), updatedFilm);
            log.info("Пользователь обновил данные по фильму в картотеке: {}, дата выпуска - {}.",
                    updatedFilm.getName(), updatedFilm.getReleaseDate());
        } else {
            throw new ValidationException("Фильм с id = " + updatedFilm.getId() + " не найден");
        }
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
