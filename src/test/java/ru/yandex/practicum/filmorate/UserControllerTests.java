package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import jakarta.validation.Validator;
import jakarta.validation.Validation;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UserControllerTests.
 */
@SpringBootTest
@Slf4j
class UserControllerTests {
    private User user;
    private static Validator validator;

    @BeforeAll
    public static void init() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @BeforeEach
    public void beforeEach() {
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
