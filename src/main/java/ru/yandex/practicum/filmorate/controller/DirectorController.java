package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorDbService;

import java.util.Collection;

@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {

    private final DirectorDbService directorDbService;

    @GetMapping
    public Collection<Director> getAll() {
        return directorDbService.findAll();
    }

    @GetMapping("/{id}")
    public Director getDirectorById(@PathVariable("id") long id) {
        return directorDbService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Director createDirector(@Valid @RequestBody Director director) {
        return directorDbService.create(director);
    }

    @PutMapping
    public Director update(@Valid @RequestBody Director director) {
        return directorDbService.update(director);
    }

    @DeleteMapping("/{id}")
    public void deleteDirector(@PathVariable("id") long id) {
        directorDbService.deleteDirector(id);
    }
}
