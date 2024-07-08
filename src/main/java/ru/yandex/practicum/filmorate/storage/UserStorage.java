package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

/**
 * UserStorage.
 */
public interface UserStorage {

    Collection<User> getAll();

    User createUser(User user);

    User update(User updatedUser);
    Optional<User> findById(Long id);
//    void setFriends(Long userId, Long friendId);
}
