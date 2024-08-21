package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorDbService;

import java.util.Collection;

/**
 * Контроллер для управления операциями с режиссерами.
 * <p>
 * Этот класс обрабатывает HTTP-запросы, связанные с сущностью "Режиссер". Он предоставляет методы для
 * получения информации о режиссерах, создания новых записей, обновления существующих и удаления
 * режиссеров из базы данных. Все операции делегируются сервису {@link DirectorDbService}.
 * <p>
 * Аннотация {@link RestController} указывает, что этот класс является контроллером REST, а аннотация
 * {@link RequestMapping} задает базовый путь для всех методов контроллера.
 * <p>
 * Аннотация {@link RequiredArgsConstructor} генерирует конструктор с обязательными аргументами для
 * инициализации зависимостей, таких как {@link DirectorDbService}.
 */
@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {

    private final DirectorDbService directorDbService;

    /**
     * Получает список всех режиссеров.
     * <p>
     * Этот метод обрабатывает HTTP GET запрос по пути "/directors" и возвращает коллекцию всех
     * режиссеров, хранящихся в базе данных.
     *
     * @return Коллекция объектов {@link Director}, представляющих всех режиссеров.
     */
    @GetMapping
    public Collection<Director> getAll() {
        return directorDbService.findAll();
    }

    /**
     * Получает информацию о режиссере по его идентификатору.
     * <p>
     * Этот метод обрабатывает HTTP GET запрос по пути "/directors/{id}" и возвращает
     * объект {@link Director} с указанным идентификатором.
     *
     * @param id Идентификатор режиссера, который необходимо получить.
     * @return Объект {@link Director} с указанным идентификатором.
     * @throws NotFoundException Если режиссер с указанным идентификатором не найден.
     */
    @GetMapping("/{id}")
    public Director getDirectorById(@PathVariable("id") long id) {
        return directorDbService.findById(id);
    }

    /**
     * Создает нового режиссера.
     * <p>
     * Этот метод обрабатывает HTTP POST запрос по пути "/directors" и создает нового
     * режиссера на основе данных, переданных в теле запроса.
     *
     * @param director Объект {@link Director}, содержащий информацию о новом режиссере.
     * @return Созданный объект {@link Director} с присвоенным идентификатором.
     * @throws ValidationException Если данные режиссера не проходят валидацию.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Director createDirector(@Valid @RequestBody Director director) {
        return directorDbService.create(director);
    }

    /**
     * Обновляет информацию о существующем режиссере.
     * <p>
     * Этот метод обрабатывает HTTP PUT запрос по пути "/directors" и обновляет информацию
     * о режиссере на основе данных, переданных в теле запроса.
     *
     * @param director Объект {@link Director}, содержащий обновленную информацию о режиссере.
     * @return Объект {@link Director} с обновленной информацией.
     * @throws NotFoundException   Если режиссер с указанным идентификатором не найден.
     * @throws ValidationException Если данные режиссера не проходят валидацию.
     */
    @PutMapping
    public Director update(@Valid @RequestBody Director director) {
        return directorDbService.update(director);
    }

    /**
     * Удаляет режиссера по его идентификатору.
     * <p>
     * Этот метод обрабатывает HTTP DELETE запрос по пути "/directors/{id}" и удаляет
     * режиссера с указанным идентификатором из базы данных.
     *
     * @param id Идентификатор режиссера, которого необходимо удалить.
     * @throws NotFoundException Если режиссер с указанным идентификатором не найден.
     */
    @DeleteMapping("/{id}")
    public void deleteDirector(@PathVariable("id") long id) {
        directorDbService.deleteDirector(id);
    }
}
