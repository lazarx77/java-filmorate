package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Collections.*;

/**
 * FilmService.
 */
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;

    public void addLike(Long filmId, Long userId) {
        filmStorage.findById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id " + filmId + " не найден"))
                .getLikes()
                .add(userId);
    }

    public void deleteLike(Long filmId, Long userId) {
        Film film = filmStorage.findById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id " + filmId + " не найден"));

        if (!film.getLikes().contains(userId)) {
            throw new NotFoundException("У фильма с id " + filmId + " нет лайка от пользователя с id " + userId);
        }
        film.getLikes().remove(userId);
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
