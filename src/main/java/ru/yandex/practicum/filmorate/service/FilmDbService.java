package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FilmDbStorage;
import ru.yandex.practicum.filmorate.dal.HistoryDbStorage;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.enums.EventTypes;
import ru.yandex.practicum.filmorate.model.enums.OperationTypes;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Сервис для работы с фильмами в базе данных.
 * Предоставляет методы для добавления, обновления, получения и удаления фильмов,
 * а также для управления лайками.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FilmDbService {

    private final FilmDbStorage filmDbStorage;
    private final MpaFieldsDbValidator mpaDbValidator;
    private final UserDbService userDbService;
    private final GenreDbService genreDbService;
    private final HistoryDbStorage historyDbStorage;

    /**
     * Возвращает коллекцию всех фильмов.
     *
     * @return Коллекция всех фильмов.
     */
    public Collection<Film> getAll() {
        return filmDbStorage.getAll();
    }

    /**
     * Добавляет новый фильм в базу данных.
     *
     * @param film Фильм, который необходимо добавить.
     * @return Добавленный фильм с присвоенным идентификатором.
     * @throws ValidationException Если данные фильма некорректны.
     */
    public Film addFilm(Film film) {
        log.info("Добавляем фильм: {}", film.getName());
        FieldsValidatorService.validateReleaseDate(film);
        mpaDbValidator.checkMpaId(film.getMpa().getId());
        log.info("Фильм {} добавлен", film.getName());

        return filmDbStorage.addFilm(film);
    }

    /**
     * Обновляет информацию о фильме.
     *
     * @param updatedFilm Фильм с обновленными данными.
     * @return Обновленный фильм.
     * @throws NotFoundException        Если фильм с указанным идентификатором не найден.
     * @throws IllegalArgumentException Если данные фильма некорректны.
     */
    public Film update(Film updatedFilm) {
        log.info("Проверка налиячия Id у фильма при обновлении: {}.", updatedFilm.getName());
        FieldsValidatorService.validateFilmId(updatedFilm);
        log.info("Проверка даты выпуска фильма при обновлении: {}.", updatedFilm.getName());
        FieldsValidatorService.validateReleaseDate(updatedFilm);
        log.info("Проверка полей фильма при обновлении: {}.", updatedFilm.getName());
        mpaDbValidator.checkMpaId(updatedFilm.getMpa().getId());
        return filmDbStorage.update(updatedFilm);
    }

    /**
     * Находит фильм по его идентификатору.
     *
     * @param id Идентификатор фильма.
     * @return Optional объекта фильма, если фильм найден, иначе - пустой объект.
     */
    public Optional<Film> findById(Long id) {
        return filmDbStorage.findById(id);
    }

    /**
     * Получает фильм по его идентификатору.
     *
     * @param id Идентификатор фильма.
     * @return Фильм с указанным идентификатором.
     * @throws NotFoundException Если фильм с указанным идентификатором не найден.
     */
    public Film getFilmById(Long id) {
        return filmDbStorage.getFilmById(id);
    }

    /**
     * Добавляет лайк к фильму от пользователя.
     *
     * @param filmId Идентификатор фильма, к которому добавляется лайк.
     * @param userId Идентификатор пользователя, который ставит лайк.
     * @throws NotFoundException Если фильм или пользователь не найдены.
     */
    public void addLike(Long filmId, Long userId) {
        log.info("Проверка существования пользователя с Id {} при добавлении like.", userId);
        userDbService.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        log.info("Проверка существования фильма с Id {} при добавлении like.", filmId);
        findById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id " + filmId + " не найден"));
        filmDbStorage.addLike(filmId, userId);
        log.info("Фильму с id {} добавлен like пользователя с id {}.", filmId, userId);
        historyDbStorage.addEvent(Event.builder()
                .userId(userId)
                .timestamp(System.currentTimeMillis())
                .eventType(EventTypes.LIKE)
                .operation(OperationTypes.ADD)
                .entityId(filmId)
                .build());
    }

    /**
     * Удаляет лайк пользователя от фильма.
     *
     * @param filmId Идентификатор фильма, у которого удаляется лайк.
     * @param userId Идентификатор пользователя, который удаляет лайк.
     * @throws NotFoundException Если фильм или пользователь не найдены.
     */
    public void deleteLike(Long filmId, Long userId) {
        log.info("Проверка существования фильма и пользователя: {} и {}", filmId, userId);
        findById(filmId).orElseThrow(() -> new NotFoundException("Фильм с id " + filmId + " не найден"));
        userDbService.findById(userId);
        filmDbStorage.deleteLike(filmId, userId);
        log.info("У фильма с id {} удален like пользователя id {}.", filmId, userId);
        historyDbStorage.addEvent(Event.builder()
                .userId(userId)
                .timestamp(System.currentTimeMillis())
                .eventType(EventTypes.LIKE)
                .operation(OperationTypes.REMOVE)
                .entityId(filmId)
                .build());
    }

    /**
     * Возвращает список самых популярных фильмов.
     *
     * @param count Количество фильмов, которые нужно вернуть.
     * @return Список из count самых популярных фильмов.
     */
    public List<Film> getPopularFilms(Integer count, Integer genreId, Integer year) {
        Optional<Integer> optionalCount = Optional.ofNullable(count);
        return getAll()
                .stream()
                .filter(film -> !film.getLikes().isEmpty())
                .sorted((film1, film2) -> Integer.compare(film2.getLikes().size(), film1.getLikes().size()))
                .filter(film -> genreId == null || film.getGenres().contains(genreDbService.findById(genreId)))
                .filter(film -> year == null || film.getReleaseDate().getYear() == year)
                .limit(optionalCount.orElse(Integer.MAX_VALUE))
                .collect(Collectors.toList());
    }

    public List<Film> getCommonFilms(long userId, long friendId) {
        Comparator<Film> comparator = Comparator.comparing(film -> film.getLikes().size(), Comparator.reverseOrder());
        return filmDbStorage.getCommonFilms(userId, friendId)
                .stream()
                .sorted(comparator)
                .toList();
    }

    /**
     * Получает список фильмов, связанных с указанным режиссером, отсортированный по заданному критерию.
     * <p>
     * Этот метод фильтрует все доступные фильмы, оставляя только те, которые связаны с режиссером
     * с указанным идентификатором. После фильтрации список фильмов сортируется в соответствии с
     * параметром `sortBy`, который определяет критерий сортировки.
     * <p>
     * Доступные критерии сортировки:
     * - "year": сортировка по дате выхода фильма (по возрастанию).
     * - "likes": сортировка по количеству лайков (по убыванию).
     * <p>
     * Если передано недопустимое значение для параметра `sortBy`, будет выброшено исключение
     * {@link IllegalArgumentException}.
     *
     * @param id     Идентификатор режиссера, для которого необходимо получить список фильмов.
     * @param sortBy Критерий сортировки списка фильмов. Может принимать значения "year" или "likes".
     * @return Список объектов {@link Film}, связанных с указанным режиссером, отсортированный
     * в соответствии с заданным критерием.
     * @throws IllegalArgumentException Если параметр sortBy имеет недопустимое значение.
     */
    public List<Film> getDirectorFilms(Long id, String sortBy) {
        Comparator<Film> comparator = switch (sortBy) {
            case "year" -> Comparator.comparing(Film::getReleaseDate);
            case "likes" -> Comparator.comparing(film -> film.getLikes().size(), Comparator.reverseOrder());
            default -> throw new IllegalArgumentException("Неправильное значение sortBy: " + sortBy);
        };

        return getAll()
                .stream()
                .filter(film -> {
                    if (film.getDirectors().isEmpty()) {
                        return false;
                    }
                    return film.getDirectors().iterator().next().getId().equals(id);
                })
                .sorted(comparator)
                .collect(Collectors.toList());
    }
}
