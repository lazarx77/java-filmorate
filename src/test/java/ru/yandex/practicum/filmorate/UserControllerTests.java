package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Класс UserControllerTests содержит тесты для контроллера пользователей.
 * Тесты проверяют валидацию полей пользователя, таких как логин, email и дата рождения.
 */
@SpringBootTest
@Slf4j
@ContextConfiguration(classes = {User.class})
class UserControllerTests {
    private User user;
    private Validator validator;

    @BeforeEach
    public void beforeEach() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        user = new User("nikname@bk.ru", "login", "name",
                LocalDate.of(2000, 1, 1));
    }

    @Test
    void loginShouldBeSpecified() {
        user.setLogin("");
        Set<ConstraintViolation<User>> validate = validator.validate(user);
        Set<String> errorMessages = validate.stream().map(ConstraintViolation::getMessage).collect(Collectors.toSet());
        log.error(errorMessages.toString());
        assertEquals(1, errorMessages.size());
    }

    @Test
    void emailShouldBeSpecified() {
        user.setEmail("sdf");
        Set<ConstraintViolation<User>> validate = validator.validate(user);
        Set<String> errorMessages = validate.stream().map(ConstraintViolation::getMessage).collect(Collectors.toSet());
        log.error(errorMessages.toString());
        assertEquals(1, errorMessages.size());
    }

    @Test
    void emailShouldNotBeBlank() {
        user.setEmail("");
        Set<ConstraintViolation<User>> validate = validator.validate(user);
        Set<String> errorMessages = validate.stream().map(ConstraintViolation::getMessage).collect(Collectors.toSet());
        log.error(errorMessages.toString());
        assertEquals(1, errorMessages.size());
    }

    @Test
    void birthDayShouldBeBeforeNow() {
        user.setBirthday(LocalDate.now());
        Set<ConstraintViolation<User>> validate = validator.validate(user);
        Set<String> errorMessages = validate.stream().map(ConstraintViolation::getMessage).collect(Collectors.toSet());
        log.error(errorMessages.toString());
        assertEquals(1, errorMessages.size());
    }
}
