package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Map;

/**
 * FieldsValidatorService - класс для валидации полей фильмов и пользователей.
 * Проверяет корректность данных при создании, обновлении и удалении объектов.
 */
public class FieldsValidatorService {

    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);

    /**
     * validateReleaseDate проверяет, что дата релиза фильма не раньше 28 декабря 1895 года.
     *
     * @param film - объект класса Film, для которого проверяется дата релиза.
     * @throws ValidationException - если дата релиза раньше 28 декабря 1895 года.
     */
    public static void validateReleaseDate(Film film) {
        if (film.getReleaseDate().isBefore(CINEMA_BIRTHDAY)) {
            throw new ValidationException("Дата релиза не может быть раньше дня рождения Кино");
        }
    }

    /**
     * validateFilmId проверяет, что у фильма задан идентификатор.
     *
     * @param film - объект класса Film, для которого проверяется наличие идентификатора.
     * @throws ValidationException - если идентификатор не задан.
     */
    public static void validateFilmId(Film film) {
        if (film.getId() == null) {
            throw new ValidationException("Id должен быть указан");
        }
    }

    /**
     * validateUpdateFilmFields проверяет уникальность фильма при обновлении.
     *
     * @param updatedFilm - объект класса Film с обновленными данными.
     * @param films       - коллекция всех существующих фильмов.
     * @throws NotFoundException   - если фильм с указанным идентификатором не найден.
     * @throws ValidationException - если обновленный фильм уже существует в коллекции.
     */
    public static void validateUpdateFilmFields(Film updatedFilm, Map<Long, Film> films) {
        if (!films.containsKey(updatedFilm.getId())) {
            throw new NotFoundException("Фильм с id = " + updatedFilm.getId() + " не найден");
        }

        if (!updatedFilm.equals(films.get(updatedFilm.getId()))) { //@EqualsAndHashCode(of = {"name", "releaseDate"})
            for (Long id : films.keySet()) {
                Film middleFilm = films.get(id);
                if (updatedFilm.equals(middleFilm)) {
                    throw new ValidationException("Этот фильм уже есть в картотеке: " + middleFilm.getName() +
                            ", дата выпуска - " + middleFilm.getReleaseDate() + ".");
                }
            }
        }

        Film oldFilm = films.get(updatedFilm.getId());

        if (updatedFilm.getName() == null) {
            updatedFilm.setName(oldFilm.getName());
        }
        if (updatedFilm.getReleaseDate() == null) {
            updatedFilm.setReleaseDate(oldFilm.getReleaseDate());
        }
        if (updatedFilm.getDescription() == null) {
            updatedFilm.setDuration(oldFilm.getDuration());
        }
    }

//    /**
//     * emailDoubleValidator проверяет уникальность пользователя.
//     *
//     * @param user  - объект класса User с обновленными данными.
//     * @param users - коллекция всех существующих всех пользователей.
//     * @throws ValidationException - если имейл уже используется.
//     */
//    public static void emailDoubleValidator(User user, Map<Long, User> users) {
//        for (Long id : users.keySet()) {
//            User middleUser = users.get(id);
//            if (user.getEmail().equals(middleUser.getEmail())) {
//                throw new ValidationException("Этот имейл уже используется");
//            }
//        }
//    }


    /**
     * validateUserId проверяет, что у пользователя задан идентификатор.
     *
     * @param user - объект класса Film, для которого проверяется наличие идентификатора.
     * @throws ValidationException - если идентификатор не задан.
     */
    public static void validateUserId(User user) {
        if (user.getId() == null) {
            throw new ValidationException("Id должен быть указан");
        }
    }

    /**
     * validateUpdateUserFields проверяет уникальность польователя при обновлении.
     *
     * @param updatedUser - объект класса User с обновленными данными.
     * @param users       - коллекция всех существующих пользователей.
     * @throws NotFoundException   - если пользователь с указанным идентификатором не найден.
     * @throws ValidationException - если обновленный пользователь (имеил) уже существует в коллекции.
     */
    public static void validateUpdateUserFields(User updatedUser, Map<Long, User> users) {

        if (!users.containsKey(updatedUser.getId())) {
            throw new NotFoundException("Польователь с id = " + updatedUser.getId() + " не найден");
        }

//        if (!updatedUser.getEmail().equals(users.get(updatedUser.getId()).getEmail())) {
//            for (Long id : users.keySet()) {
//                User middleUser = users.get(id);
//                if (updatedUser.getEmail().equals(middleUser.getEmail())) {
//                    throw new ValidationException("Имейл " + updatedUser.getEmail() + " уже присвоен другому " +
//                            "пользователю: " + middleUser.getLogin());
//                }
//            }
//        }

        User oldUser = users.get(updatedUser.getId());

        if (updatedUser.getLogin() == null) {
            updatedUser.setLogin(oldUser.getLogin());
        }

        if (updatedUser.getName() == null) {
            updatedUser.setName(oldUser.getName());
        }
        if (updatedUser.getEmail() == null) {
            updatedUser.setEmail(oldUser.getEmail());
        }

        if (updatedUser.getBirthday() == null) {
            updatedUser.setBirthday(oldUser.getBirthday());
        }
    }

    public static void validateDirectorId(Director director) {
        if (director.getId() == null) {
            throw new ValidationException("Id должен быть указан");
        }
    }
}
