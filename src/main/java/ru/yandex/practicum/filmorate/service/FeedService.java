package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.HistoryDbStorage;
import ru.yandex.practicum.filmorate.dal.UserDbStorage;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Сервис для работы с лентой событий пользователей.
 * <p>
 * Данный класс предоставляет методы для получения ленты событий, связанных с
 * конкретным пользователем. Он использует {@link HistoryDbStorage} для доступа
 * к данным о событиях и {@link UserDbStorage} для проверки существования пользователя.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class FeedService {

    private final HistoryDbStorage historyDbStorage;
    private final UserDbStorage userDbStorage;

    /**
     * Получает ленту событий для указанного пользователя.
     * <p>
     * Метод проверяет существование пользователя по его идентификатору и
     * возвращает список событий, связанных с этим пользователем. Если пользователь
     * не существует, будет выброшено исключение.
     * </p>
     *
     * @param userId Идентификатор пользователя, для которого нужно получить ленту событий.
     * @return Список событий, связанных с указанным пользователем.
     * @throws NotFoundException Если пользователь с указанным идентификатором не найден.
     */
    public List<Event> getFeed(long userId) {
        userDbStorage.getUserById(userId);
        return new ArrayList<>(historyDbStorage.getEventsByUser(userId));
    }
}
