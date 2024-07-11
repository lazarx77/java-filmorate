package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * FilmService.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public void addLike(Long filmId, Long userId) {
        userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        filmStorage.findById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id " + filmId + " не найден"))
                .getLikes()
                .add(userId);
        log.info("Фильму с id {} добавлен like пользователя с id {}.", filmId, userId);
    }

    public void deleteLike(Long filmId, Long userId) {
        Film film = filmStorage.findById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id " + filmId + " не найден"));

        if (!film.getLikes().contains(userId)) {
            throw new NotFoundException("У фильма с id " + filmId + " нет лайка от пользователя с id " + userId);
        }
        film.getLikes().remove(userId);
        log.info("У фильма с id {} удален like пользователя id {}.", filmId, userId);
    }

    public List<Film> getMostLiked(int count) {
        Comparator<Film> comparator = Comparator.comparing(film -> film.getLikes().size(), Comparator.reverseOrder());
        return filmStorage.getAll()
                .stream()
                .sorted(comparator)
                .limit(count)
                .collect(Collectors.toList());
    }
}
