package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;

/**
 * Контроллер для управления пользователями.
 * Предоставляет методы для получения списка всех пользователей, добавления и удаления друзей,
 * получения списка друзей и общих друзей, получения пользователя по идентификатору,
 * создания и обновления пользователя.
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserStorage userStorage;
    private final UserService userService;

    /**
     * getAll - получает список всех пользователей.
     *
     * @return Коллекция всех пользователей.
     */
    @GetMapping
    public Collection<User> getAll() {
        return userStorage.getAll();
    }

    /**
     * addFriend - добавляет пользователя с идентификатором friendId в список друзей пользователя с идентификатором id.
     *
     * @param id       Идентификатор пользователя.
     * @param friendId Идентификатор друга.
     */
    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") long id, @PathVariable("friendId") long friendId) {
        userService.addFriend(id, friendId);
    }

    /**
     * deleteFriend - удаляет пользователя с идентификатором friendId из списка друзей пользователя с идентификатором id.
     *
     * @param id       Идентификатор пользователя.
     * @param friendId Идентификатор друга.
     */
    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable("id") long id, @PathVariable("friendId") long friendId) {
        userService.deleteFriend(id, friendId);
    }

    /**
     * getUserFriends - получает список друзей пользователя с идентификатором id.
     *
     * @param id Идентификатор пользователя.
     * @return Список друзей пользователя.
     */
    @GetMapping("/{id}/friends")
    public List<User> getUserFriends(@PathVariable("id") long id) {
        return userService.getUserFriends(id);
    }

    /**
     * getCommonFriends - получает список общих друзей пользователей с идентификаторами id и otherId.
     *
     * @param id      Идентификатор первого пользователя.
     * @param otherId Идентификатор второго пользователя.
     * @return Список общих друзей.
     */
    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable("id") long id, @PathVariable("otherId") long otherId) {
        return userService.getCommonFriends(id, otherId);
    }

    /**
     * findById - получает пользователя с идентификатором id.
     *
     * @param id Идентификатор пользователя.
     * @return Пользователь с указанным идентификатором.
     * @throws NotFoundException Если пользователь с указанным идентификатором не найден.
     */
    @GetMapping("/{id}")
    public User findById(@PathVariable("id") long id) {
        return userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
    }

    /**
     * createUser - создает нового пользователя.
     *
     * @param user Объект пользователя для создания.
     * @return Созданный пользователь.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@Valid @RequestBody User user) {
        return userStorage.createUser(user);
    }

    /**
     * update - обновляет существующего пользователя.
     *
     * @param updatedUser Объект пользователя с обновленными данными.
     * @return Обновленный пользователь.
     */
     @PutMapping
    public User update(@Valid @RequestBody User updatedUser) {
        return userStorage.update(updatedUser);
    }
}
