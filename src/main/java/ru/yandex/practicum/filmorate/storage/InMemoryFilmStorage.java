package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FieldsValidatorService;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Класс InMemoryFilmStorage реализует интерфейс FilmStorage и предоставляет методы для работы с фильмами в памяти
 * приложения.
 * Фильмы хранятся в HashMap, где ключом является идентификатор фильма, а значением - объект Film.
 * Класс предоставляет методы для добавления новых фильмов, получения всех фильмов, обновления существующих фильмов.
 * При добавлении и обновлении фильмов выполняется проверка корректности данных с помощью сервиса
 * FieldsValidatorService.
 */
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private static final Map<Long, Film> films = new HashMap<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public Film addFilm(Film film) {
        log.info("Проверка даты выпуска фильма при добавлении в картотеку: {}.", film.getName());
        FieldsValidatorService.validateReleaseDate(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Пользователь добавил фильм в картотеку: {}, дата выпуска - {}.", film.getName(),
                film.getReleaseDate());
        return films.get(film.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Film> getAll() {
        return films.values();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Film update(Film updatedFilm) {
        log.info("Проверка налиячия Id у фильма при обновлении: {}.", updatedFilm.getName());
        FieldsValidatorService.validateFilmId(updatedFilm);

        log.info("Проверка даты выпуска фильма при обновлении: {}.", updatedFilm.getName());
        FieldsValidatorService.validateReleaseDate(updatedFilm);

        log.info("Проверка полей фильма при обновлении: {}.", updatedFilm.getName());
        FieldsValidatorService.validateUpdateFilmFields(updatedFilm, films);

        films.put(updatedFilm.getId(), updatedFilm);
        log.info("Пользователь обновил данные по фильму в картотеке: {}, дата выпуска - {}.",
                updatedFilm.getName(), updatedFilm.getReleaseDate());

        return films.get(updatedFilm.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Film> findById(Long id) {
        return films.values().stream().filter(p -> p.getId().equals(id)).findAny();
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
