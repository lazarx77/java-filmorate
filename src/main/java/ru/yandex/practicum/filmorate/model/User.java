package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * User представляет пользователя.
 * Содержит информацию о пользователе, такую как идентификатор, электронная почта, логин, имя и дата рождения.
 * Хранит поля:
 * id - идентификатор пользователя
 * friends - список идентификаторов друзей пользователя
 * email - электронная почта пользователя
 * login - логин пользователя
 * name - имя пользователя
 * birthday - дата рождения пользователя
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(of = {"email"})
public class User {

    private Long id;
    private Set<Long> friends = new HashSet<>();

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
