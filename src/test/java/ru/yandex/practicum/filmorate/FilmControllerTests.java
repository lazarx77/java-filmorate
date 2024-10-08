package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * FilmControllerTests - класс для тестирования контроллера фильмов.
 */
@SpringBootTest
@Slf4j
@ContextConfiguration(classes = {Film.class})
class FilmControllerTests {
    private Film film;
    private Validator validator;
    private final FilmStorage filmStorage = new InMemoryFilmStorage();


    @BeforeEach
    public void beforeEach() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        Mpa mpa = new Mpa();
        mpa.setId(1);
        film = new Film();
        film.setName("name");
        film.setDescription("description");
        film.setReleaseDate(LocalDate.of(2022, 1, 1));
        film.setDuration(90L);
        film.setMpa(mpa);
    }

    @Test
    void nameShouldBeSpecified() {
        film.setName("");
        Set<ConstraintViolation<Film>> validate = validator.validate(film);
        Set<String> errorMessages = validate.stream().map(ConstraintViolation::getMessage).collect(Collectors.toSet());
        log.error(errorMessages.toString());
        assertEquals(1, errorMessages.size());
    }

    @Test
    void descriptionShouldBeLessThan200() {
        film.setDescription("a;sldkfna;sldkfa ;slkdjf;alskdjf;alskjdf;laksjd ;flkajsdl;kfja;sldkfj a;slkdfj ;alskdj" +
                "askljdfhlaskjhflavksjdflasjhdlfkajhsldkjcfhalsjdhflaksjhdflkajschldjkfhalskdjhfclaksjdbhlfajshlfkasd" +
                "askl;djfhalskdjfhaklsdhfklasjhdlfkajshdlfkajhsdkfjalksjcaksflasnldfkjcahlskfjhlaksjdhncflakjsldfkasl");
        Set<ConstraintViolation<Film>> validate = validator.validate(film);
        Set<String> errorMessages = validate.stream().map(ConstraintViolation::getMessage).collect(Collectors.toSet());
        log.error(errorMessages.toString());
        assertEquals(1, errorMessages.size());
    }

    @Test
    void dateShouldBeAfterCinemaBirthday() {
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        String errorMassage = "Дата релиза не может быть раньше дня рождения Кино";

        try {
            filmStorage.addFilm(film);
        } catch (ValidationException e) {
            log.error(errorMassage);
        }
    }

    @Test
    void durationShouldBePositive() {
        film.setDuration(0L);
        Set<ConstraintViolation<Film>> validate = validator.validate(film);
        Set<String> errorMessages = validate.stream().map(ConstraintViolation::getMessage).collect(Collectors.toSet());
        log.error(errorMessages.toString());
        assertEquals(1, errorMessages.size());
    }
}
