package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.Set;

/**
 * User.
 */
@Data
@EqualsAndHashCode(of = {"email"})
public class User {

    private Long id;
    private Set<Long> friends;

    @NotEmpty(message = "Электронная почта не может быть пустой")
    @NotNull(message = "Электронная почта не может быть Null")
    @Email(message = "Электронная почта должна быть пустой и должна соответствовать формату электронной почты")
    private String email;
    @NotBlank(message = "Логин не может быть пустым и содержать пробелы")
    private String login;
    private String name;
    @Past(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;

    public User(String email, String login, String name, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }
}
