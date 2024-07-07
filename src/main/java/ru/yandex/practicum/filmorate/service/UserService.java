package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * UserService.
 */

@Service
public class UserService {

    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }
    public void addFriend(User user, User friend) {
        user.getFriends().add(friend.getId());
        friend.getFriends().add(user.getId());
    }

    public void deleteFriend(User user, User friend) {
        if (!user.getFriends().contains(friend.getId())) {
            throw new NotFoundException("У пользователя " + user.getId() + " нет друга " + friend.getId());
        }
        user.getFriends().remove(friend.getId());
        friend.getFriends().remove(user.getId());
    }

    public List<User> getUserFriends(User user) {
        return user.getFriends().stream()
                .map(userStorage::findById)
                .map(Optional::orElseThrow)
                .collect(Collectors.toList());
    }
}
