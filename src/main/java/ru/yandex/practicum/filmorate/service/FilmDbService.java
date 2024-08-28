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
    private final DirectorDbService directorDbService;

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
        log.info("Проверка существования фильма в базе данных: {}.", updatedFilm.getName());
        if (filmDbStorage.findById(updatedFilm.getId()).isEmpty()) {
            throw new NotFoundException("Фильм с id " + updatedFilm.getId() + " не найден");
        }
        ;
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
    public void addRating(Long filmId, Long userId, Integer userRating) {
        log.info("Проверка существования пользователя с Id {} при добавлении like.", userId);
        userDbService.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        log.info("Проверка существования фильма с Id {} при добавлении like.", filmId);
        findById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id " + filmId + " не найден"));
        filmDbStorage.addRating(filmId, userId, userRating);
        log.info("Фильму с id {} добавлен like пользователя с id {}.", filmId, userId);
        saveHistory(filmId, userId, OperationTypes.ADD);
    }

    /**
     * Удаляет лайк пользователя от фильма.
     *
     * @param filmId Идентификатор фильма, у которого удаляется лайк.
     * @param userId Идентификатор пользователя, который удаляет лайк.
     * @throws NotFoundException Если фильм или пользователь не найдены.
     */
    public void deleteRating(Long filmId, Long userId) {
        log.info("Проверка существования фильма и пользователя: {} и {}", filmId, userId);
        findById(filmId).orElseThrow(() -> new NotFoundException("Фильм с id " + filmId + " не найден"));
        userDbService.findById(userId);
        filmDbStorage.deleteRating(filmId, userId);
        log.info("У фильма с id {} удален like пользователя id {}.", filmId, userId);
        saveHistory(filmId, userId, OperationTypes.REMOVE);
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
                //.filter(film -> !film.getLikes().isEmpty())
                .sorted((film1, film2) -> Double.compare(film2.getFilmRating(), film1.getFilmRating()))
                .filter(film -> genreId == null || film.getGenres().contains(genreDbService.findById(genreId)))
                .filter(film -> year == null || film.getReleaseDate().getYear() == year)
                .limit(optionalCount.orElse(Integer.MAX_VALUE))
                .collect(Collectors.toList());
    }

    /**
     * Получает список общих фильмов между двумя пользователями, отсортированный по количеству лайков.
     * <p>
     * Данный метод извлекает фильмы, которые оба пользователя (пользователь и его друг) оценили,
     * и сортирует их в порядке убывания количества лайков. Сначала выполняется запрос к базе данных
     * для получения общих фильмов, после чего результаты сортируются с использованием компаратора,
     * который сравнивает количество лайков для каждого фильма.
     * </p>
     *
     * @param userId   Идентификатор пользователя, для которого запрашивается список общих фильмов.
     * @param friendId Идентификатор друга, с которым сравниваются фильмы.
     * @return Список общих фильмов между указанным пользователем и его другом, отсортированный по количеству лайков.
     */
    public List<Film> getCommonFilms(long userId, long friendId) {
        Comparator<Film> comparator = Comparator.comparing(film -> film.getUsersRatedFilm().size(), Comparator.reverseOrder());
        return filmDbStorage.getCommonFilms(userId, friendId)
                .stream()
                .sorted(comparator)
                .toList();
    }

    /**
     * Удаляет фильм и все связанные с ним записи из таблиц.
     *
     * @param filmId Идентификатор фильма.
     */
    public void deleteFilm(long filmId) {
        filmDbStorage.deleteFilm(filmId);
        log.info("Фильм с id {} удален.", filmId);
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
        log.info("проверка существования режиссера с id {}.", id);
        directorDbService.findById(id);
        Comparator<Film> comparator = switch (sortBy) {
            case "year" -> Comparator.comparing(Film::getReleaseDate);
            case "likes" -> Comparator.comparing(Film::getFilmRating, Comparator.reverseOrder());
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

    /**
     * Сохраняет информацию о событии в истории действий пользователя.
     * <p>
     * Данный метод создает и сохраняет новое событие в базе данных, связанное с
     * определенной операцией, выполненной пользователем. Событие включает идентификатор
     * пользователя, временную метку, тип события и тип операции. Метод использует
     * {@link HistoryDbStorage} для добавления события в историю.
     * </p>
     *
     * @param id             Идентификатор сущности, с которой связано событие (например, идентификатор фильма).
     * @param userId         Идентификатор пользователя, который выполнил операцию.
     * @param operationTypes Тип операции, связанной с событием (например, добавление или удаление лайка).
     */
    private void saveHistory(Long id, Long userId, OperationTypes operationTypes) {
        historyDbStorage.addEvent(Event.builder()
                .userId(userId)
                .timestamp(System.currentTimeMillis())
                .eventType(EventTypes.LIKE)
                .operation(operationTypes)
                .entityId(id)
                .build());
    }

    /**
     * searchFilm - поиск фильмов по названию и режиссеру.
     *
     * @param query значаение для поиска
     * @param by    поиск выполнять по названию фильма, режиссера или вместе
     * @return результат поиска
     */
    public List<Film> searchFilms(String query, String by) {
        log.info("Поиск фильмов по запросу: {} в: {}", query, by);

        String[] searchBy = by.split(",");
        return filmDbStorage.getAll().stream()
                .filter(film -> {
                    boolean matchTitle = searchBy.length == 1 && searchBy[0].equalsIgnoreCase("title")
                            && film.getName().toLowerCase().contains(query.toLowerCase());
                    boolean matchDirector = searchBy.length == 1 && searchBy[0].equalsIgnoreCase("director")
                            && film.getDirectors().stream()
                            .anyMatch(director -> director.getName().toLowerCase().contains(query.toLowerCase()));
                    boolean matchBoth = searchBy.length == 2 &&
                            (film.getName().toLowerCase().contains(query.toLowerCase()) ||
                                    film.getDirectors().stream()
                                            .anyMatch(director -> director.getName().toLowerCase().contains(query.toLowerCase())));
                    return matchTitle || matchDirector || matchBoth;
                })
                .sorted(Comparator.comparing((Film film) -> {
                    boolean directorMatch = searchBy.length == 2 && film.getDirectors().stream()
                            .anyMatch(director -> director.getName().toLowerCase().contains(query.toLowerCase()));
                    return directorMatch ? 0 : 1;
                }))
                .collect(Collectors.toList());
    }

    /**
     * Получает рекомендации фильмов для указанного пользователя.
     * <p>
     * Данный метод извлекает список рекомендованных фильмов на основе предпочтений
     * пользователя с заданным идентификатором. Рекомендации формируются на основе
     * анализа данных о просмотренных фильмах и лайках пользователя. Метод использует
     * {@link FilmDbStorage} для получения данных о рекомендациях.
     * </p>
     *
     * @param id Идентификатор пользователя, для которого запрашиваются рекомендации фильмов.
     * @return Список рекомендованных фильмов для указанного пользователя.
     */
    public List<Film> getRecommendations(long id) {
        return filmDbStorage.getRecommendations(id);
    }
}
