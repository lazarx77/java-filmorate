package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.GenreDbService;
import ru.yandex.practicum.filmorate.service.MpaDbService;

import java.util.Collection;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {
    private final GenreDbService genreService;

    @GetMapping
    public Collection<Genre> getAll() {
        return genreService.findAll();
    }

    @GetMapping("/{id}")
    public Genre getGenreById(@PathVariable("id") int id) {
        return genreService.findById(id);
    }
}
