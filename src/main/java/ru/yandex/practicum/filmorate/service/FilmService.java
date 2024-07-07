package ru.yandex.practicum.filmorate.service;

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
public class FilmService {

    private final FilmStorage filmStorage;
    private final static int MOST_LIKED_SIZE =10;

    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public void addLike(Film film, User user) {
        film.getLikes().add(user.getId());
    }

    public void deleteLike(Film film, User user) {
        if (!film.getLikes().contains(user.getId())) {
            throw new NotFoundException("У фильма " + film.getName() + " нет лайка от пользователя " + user.getId());
        }
        film.getLikes().remove(user.getId());
    }

    public List<Film> getMostLiked() {
        Comparator<Film> comparator = Comparator.comparing(film -> film.getLikes().size(), Comparator.reverseOrder());
        return filmStorage.getAll()
                .stream()
                .sorted(comparator)
                .limit(MOST_LIKED_SIZE)
                .collect(Collectors.toList());
    }
}
