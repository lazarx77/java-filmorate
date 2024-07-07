package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FieldsValidatorService;

import java.util.*;

/**
 * InMemoryFilmStorage.
 */
@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private static final Map<Long, Film> films = new HashMap<>();

    @Override
    public Film addFilm(Film film) {
        // проверяем выполнение необходимых условий
        log.info("Проверка даты выпуска фильма при добавлении в картотеку: {}.", film.getName());
        FieldsValidatorService.validateReleaseDate(film);

        // формируем дополнительные данные
        film.setId(getNextId());
        // сохраняем новый фильм в памяти приложения
        films.put(film.getId(), film);
        log.info("Пользователь добавил фильм в картотеку: {}, дата выпуска - {}.", film.getName(),
                film.getReleaseDate());
        return films.get(film.getId());
    }

    @Override
    public Collection<Film> getAll() {
        return films.values();
    }

    @Override
    public Film update(Film updatedFilm) {
        // проверяем необходимые условия
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

//    @Override
//    public List<Film> getLikes(){
//        List<Film> likes = new ArrayList<>();
//        for (Film film : films.values()) {
//            if (!film.getLikes().isEmpty()) {
//                likes.add(film);
//            }
//        }
//        return likes;
//    }
}
