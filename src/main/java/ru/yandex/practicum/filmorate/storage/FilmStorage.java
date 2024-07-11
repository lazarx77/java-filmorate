package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

/**
 * FilmStorage.
 */
public interface FilmStorage {
    Collection<Film> getAll();

    Film addFilm(Film film);

    Film update(Film updatedFilm);

    Optional<Film> findById(Long id);
}
