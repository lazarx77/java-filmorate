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
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        if (user.getFriends() != null) {
            throw new NotFoundException("У пользователя " + userId + " нет друзей");
        }

        User friend = userStorage.findById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + friendId + " не найден"));
        if (friend.getFriends() == null) {
            throw new NotFoundException("У пользователя с id " + friendId + " нет друзей");
        }

        if (!user.getFriends().contains(friendId)) {
            throw new NotFoundException("У пользователя " + userId + " нет друга " + friendId);
        }

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    public List<User> getUserFriends(Long userId) {
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        if (user.getFriends() == null) {
            throw new NotFoundException("У пользователя с id " + userId + " нет друзей");
        }
        List<User> friendsList = new ArrayList<>();
        for (Long id : user.getFriends()) {
            friendsList.add(userStorage.findById(id)
                    .orElseThrow(() -> new NotFoundException("Пользователя с id " + id + " не существует")));
        }
        return friendsList;
    }
}
