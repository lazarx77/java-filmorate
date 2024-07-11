package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.List;

/**
 * FilmController.
 */
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmStorage filmStorage;
    private final FilmService filmService;

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable("id") long id, @PathVariable("userId") long userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") long id, @PathVariable("userId") long userId) {
        filmService.deleteLike(id, userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film addFilm(@Valid @RequestBody Film film) {
        return filmStorage.addFilm(film);
    }

    @GetMapping("/popular")
    public List<Film> getPopular(@PathVariable("count") @RequestParam(defaultValue = "10") int count) {
        return filmService.getMostLiked(count);
    }

    @GetMapping
    public Collection<Film> getAll() {
        return filmStorage.getAll();
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film updatedFilm) {
        return filmStorage.update(updatedFilm);
    }
}
