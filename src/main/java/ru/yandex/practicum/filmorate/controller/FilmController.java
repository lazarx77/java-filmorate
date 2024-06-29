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

    private static final Map<Long, Film> films = new HashMap<>();

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        // проверяем выполнение необходимых условий
        log.info("Проверка даты выпуска фильма при добавлении в картотеку: {}.", film.getName());
        FilmValidator.validateReleaseDate(film);

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
        FilmValidator.validateId(updatedFilm);

        log.info("Проверка даты выпуска фильма при обновлении: {}.", updatedFilm.getName());
        FilmValidator.validateReleaseDate(updatedFilm);

        log.info("Проверка полей фильма при обновлении: {}.", updatedFilm.getName());
        FilmValidator.validateUpdateFields(updatedFilm, films);

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

    /**
     * FilmValidator, утилитарный класс для валидации полей фильмов.
     */
    static final class FilmValidator {

        private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);

        static void validateReleaseDate(Film film) {
            if (film.getReleaseDate().isBefore(CINEMA_BIRTHDAY)) {
                throw new ValidationException("Дата релиза не может быть раньше дня рождения Кино");
            }
        }

        static void validateId(Film film) {
            if (film.getId() == null) {
                throw new ValidationException("Id должен быть указан");
            }
        }

        static void validateUpdateFields(Film updatedFilm, Map<Long, Film> films) {

            if (!films.containsKey(updatedFilm.getId())) {
                throw new ValidationException("Фильм с id = " + updatedFilm.getId() + " не найден");
            }

            //проверяем на дубликат фильма при обновлении
            if (!updatedFilm.equals(films.get(updatedFilm.getId()))) { //@EqualsAndHashCode(of = {"name", "releaseDate"})
                for (Long id : films.keySet()) {
                    Film middleFilm = films.get(id);
                    if (updatedFilm.equals(middleFilm)) {
                        throw new ValidationException("Этот фильм уже есть в картотеке: " + middleFilm.getName() +
                                ", дата выпуска - " + middleFilm.getReleaseDate() + ".");
                    }
                }
            }

            Film oldFilm = films.get(updatedFilm.getId());

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
        }
    }
}


