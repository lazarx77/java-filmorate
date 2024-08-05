package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Map;

@Slf4j
public class FilmFieldsDbValidatorService extends BaseRepository<Film> {

    public FilmFieldsDbValidatorService(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    public void validateUpdateFilmFields(Film updatedFilm) {
        if (findOne("SELECT * FROM FILMS WHERE FILM_ID = ?", updatedFilm.getId()).isEmpty()) {
            throw new NotFoundException("Фильм с id = " + updatedFilm.getId() + " не найден");
        }

        if (findOne("SELECT * FROM FILMS WHERE FILM_ID = ?", updatedFilm.getId()).get().equals(updatedFilm)) {
            throw new ValidationException("Фильм с таким именем " + updatedFilm.getName() + "и датой релиза " +
                    updatedFilm.getReleaseDate() + " уже есть в картотеке.");
        }

//        if (!updatedFilm.equals(films.get(updatedFilm.getId()))) { //@EqualsAndHashCode(of = {"name", "releaseDate"})
//            for (Long id : films.keySet()) {
//                Film middleFilm = films.get(id);
//                if (updatedFilm.equals(middleFilm)) {
//                    throw new ValidationException("Этот фильм уже есть в картотеке: " + middleFilm.getName() +
//                            ", дата выпуска - " + middleFilm.getReleaseDate() + ".");
//                }
//            }
//        }
    }


//    public void checkFilmFieldsOnCreate(Film film) {
//        log.info("Проверка полей фильмов при его создании; {}", film.getName());
//        String FIND_BY_EMAIL = "SELECT * FROM FILMS WHERE EMAIL =?";
//        findOne(FIND_BY_EMAIL, user.getEmail());
//        if (findOne(FIND_BY_EMAIL, user.getEmail()).isPresent()) {
//            throw new ValidationException("Этот имейл " + user.getEmail() + " уже используется");
//        }
//        String FIND_BY_LOGIN = "SELECT * FROM USERS WHERE LOGIN = ?";
//        if (findOne(FIND_BY_LOGIN, user.getLogin()).isPresent()) {
//            throw new ValidationException("Пользователь с таким логином " + user.getLogin() + " уже существует.");
//        }
//    }
}
