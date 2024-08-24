package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.DirectorDbStorage;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

/**
 * Сервис для работы с данными о режиссерах в базе данных.
 * <p>
 * Данный класс предоставляет методы для выполнения операций CRUD (создание, чтение, обновление, удаление)
 * с объектами типа {@link Director}. Он использует {@link DirectorDbStorage} для взаимодействия с базой данных
 * и {@link DirectorDbValidatorService} для валидации данных о режиссерах.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DirectorDbService {

    private final DirectorDbStorage directorDbStorage;
    private final DirectorDbValidatorService directorDbValidator;

    /**
     * Получает список всех режиссеров из базы данных.
     *
     * @return Список всех режиссеров.
     */
    public List<Director> findAll() {
        return directorDbStorage.findAll();
    }

    /**
     * Находит режиссера по его идентификатору.
     *
     * @param id Идентификатор режиссера.
     * @return Режиссер с указанным идентификатором, или null, если не найден.
     */
    public Director findById(Long id) {
        return directorDbStorage.findById(id);
    }

    /**
     * Получает имя режиссера по его идентификатору.
     *
     * @param id Идентификатор режиссера.
     * @return Имя режиссера с указанным идентификатором.
     * @throws NotFoundException Если режиссер с указанным идентификатором не найден.
     */
    public String findDirectorNameById(Long id) {
        return findById(id).getName();
    }

    /**
     * Создает нового режиссера в базе данных.
     *
     * @param director Объект режиссера, который нужно создать.
     * @return Созданный режиссер с установленным идентификатором.
     * @throws ValidationException Если имя режиссера некорректно.
     */
    public Director create(Director director) {
        directorDbValidator.checkDirectorNameField(director);
        return directorDbStorage.createDirector(director);
    }

    /**
     * Обновляет данные существующего режиссера в базе данных.
     *
     * @param director Объект режиссера с обновленными данными.
     * @return Обновленный режиссер.
     * @throws ValidationException Если идентификатор или имя режиссера некорректны
     * @throws NotFoundException   Если режиссер не найден.
     */
    public Director update(Director director) {
        log.info("Проверка наличия id режиссера");
        FieldsValidatorService.validateDirectorId(director);
        directorDbValidator.checkDirectorId(director.getId());
        directorDbValidator.checkDirectorNameField(director);
        return directorDbStorage.update(director);
    }

    /**
     * Удаляет режиссера из базы данных по его идентификатору.
     *
     * @param id Идентификатор режиссера, которого нужно удалить.
     * @throws NotFoundException Если режиссер с указанным идентификатором не найден.
     */
    public void deleteDirector(Long id) {
        log.info("Проверка существования режиссера с id: {}", id);
        findById(id);
        directorDbStorage.delete(id);
        log.info("Режиссер с id {} удален.", id);
    }

    /**
     * Находит всех режиссеров, связанных с указанным фильмом.
     *
     * @param filmId Идентификатор фильма.
     * @return Список режиссеров, связанных с указанным фильмом.
     */
    public List<Director> findDirectorsByFilmId(Long filmId) {
        return directorDbStorage.findDirectorsByFilmId(filmId);
    }
}
