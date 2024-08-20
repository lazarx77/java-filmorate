package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.DirectorDbStorage;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DirectorDbService {

    private final DirectorDbStorage directorDbStorage;
    private final DirectorDbValidatorService directorDbValidator;
//    private final FieldsValidatorService;

    public List<Director> findAll() {
        return directorDbStorage.findAll();
    }

    public Director findById(Long id) {
        return directorDbStorage.findById(id);
    }

    public String findDirectorNameById(Long id) {
        return findById(id).getName();
    }

    public Director create(Director director) {
        directorDbValidator.checkDirectorNameField(director);
        return directorDbStorage.createDirector(director);
    }

    public Director update(Director director) {
        log.info("Проверка наличия id режиссера");
        FieldsValidatorService.validateDirectorId(director);
        directorDbValidator.checkDirectorId(director.getId());
        directorDbValidator.checkDirectorNameField(director);
        return directorDbStorage.update(director);
    }

    public void deleteDirector(Long id) {
        log.info("Проверка существования режиссера с id: {}",id);
        findById(id);
        directorDbStorage.delete(id);
        log.info("Режиссер с id {} удален.", id);
    }
}
