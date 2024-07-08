package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

/**
 * UserService.
 */

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public void addFriend(Long userId, Long friendId) {
        // Находим пользователя по userId, если не найден, выбрасываем исключение
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        // Находим друга по friendId, если не найден, выбрасываем исключение
        User friend = userStorage.findById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + friendId + " не найден"));

        // Добавляем друга в список друзей пользователя
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    public List<Long> getCommonFriends(Long userId, Long otherId) {
        Set<Long> userFriendsSet = new HashSet<>(userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден")).getFriends());
        Set<Long> otherUserFriendsSet = new HashSet<>(userStorage.findById(otherId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + otherId + " не найден")).getFriends());

        userFriendsSet.retainAll(otherUserFriendsSet);

        return new ArrayList<>(userFriendsSet);
    }

    public void deleteFriend(Long userId, Long friendId) {
        // Находим пользователя по userId, если не найден, выбрасываем исключение
        if (!userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"))
                .getFriends().contains(friendId)) {
            throw new NotFoundException("У пользователя " + userId + " нет друга " + friendId);
        }
        userStorage.findById(userId).orElseThrow().getFriends().remove(friendId);
        userStorage.findById(friendId).orElseThrow().getFriends().remove(userId);
    }

    public Set<Long> getUserFriends(Long userId) {
        return userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден")).getFriends();
    }
}
