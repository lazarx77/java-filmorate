package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Сервис для управления пользователями и их друзьями.
 * Позволяет добавлять и удалять друзей, а также получать список общих друзей для двух пользователей.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserStorage userStorage;

    /**
     * addFriend - добавляет пользователя с идентификатором friendId в список друзей пользователя с
     * идентификатором userId.
     *
     * @param userId   идентификатор пользователя
     * @param friendId идентификатор друга, которого нужно добавить
     * @throws NotFoundException если пользователь или друг не найдены
     */
    public void addFriend(Long userId, Long friendId) {
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        User friend = userStorage.findById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + friendId + " не найден"));

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        log.info("Пользователь с id {} добавил в друзья пользователя с id {}.", userId, friendId);
    }

    /**
     * getCommonFriends - возвращает список общих друзей для пользователей с идентификаторами userId и otherId.
     *
     * @param userId  идентификатор первого пользователя
     * @param otherId идентификатор второго пользователя
     * @return список общих друзей
     * @throws NotFoundException если один из пользователей не найден
     */
    public List<User> getCommonFriends(Long userId, Long otherId) {
        Set<Long> userFriendsSet = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"))
                .getFriends();
        Set<Long> otherUserFriendsSet = userStorage.findById(otherId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + otherId + " не найден"))
                .getFriends();

        Set<Long> common = new HashSet<>(userFriendsSet);
        common.retainAll(otherUserFriendsSet);

        return common.stream()
                .map(id -> userStorage.findById(id)
                        .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден")))
                .collect(Collectors.toList());
    }

    /**
     * deleteFriend - удаляет пользователя с идентификатором friendId из списка друзей пользователя с идентификатором
     * userId.
     *
     * @param userId   идентификатор пользователя
     * @param friendId идентификатор друга, которого нужно удалить
     * @throws NotFoundException если пользователь не найден или у него нет друзей
     */
    public void deleteFriend(Long userId, Long friendId) {
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        if (user.getFriends() == null) {
            throw new NotFoundException("У пользователя " + userId + " нет друзей");
        }

        User friend = userStorage.findById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + friendId + " не найден"));
        if (friend.getFriends() == null) {
            throw new NotFoundException("У пользователя с id " + friendId + " нет друзей");
        }

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        log.info("Пользователь с id {} удалил их друзей пользователя с id {}.", userId, friendId);
    }

    /**
     * getUserFriends - получает список всех друзей пользователя с идентификатором userId.
     *
     * @param userId идентификатор пользователя
     * @throws NotFoundException если пользователь не найден или у него нет друзей
     */
    public List<User> getUserFriends(Long userId) {
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        if (user.getFriends() == null) {
            throw new NotFoundException("У пользователя с id " + userId + " нет друзей");
        }
        return user.getFriends().stream()
                .map(id -> userStorage.findById(id)
                        .orElseThrow(() -> new NotFoundException("Пользователя с id " + id + " не существует")))
                .collect(Collectors.toList());
    }
}
