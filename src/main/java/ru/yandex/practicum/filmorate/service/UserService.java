package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
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

    //todo: fix this
    public List<User> getCommonFriends(Long userId, Long otherId) {

//        List<User> userFriendsList = getUserFriends(userId);
//        List<User> otherFriendsList = getUserFriends(otherId);
//        List<User> commonFriendsList = new ArrayList<>();
//
//        for (User user : userFriendsList) {
//            for(User otherUser : otherFriendsList) {
//                if (user.getFriends().contains(otherUser.getFriends())) {
//                    commonFriendsList.add(user);
//                }
//            }
////            if (user.getFriends().contains(otherId)) {
////                userFriendsList.add(user);
////            }
//        }
//
//
////        if (userFriendsList.stream().noneMatch(user -> user.getId().equals(otherId))) {
////            throw new NotFoundException("Пользователь с id " + otherId + " не является друзьями пользователя с id " + userId);
////        }
//
//
        Set<Long> userFriendsSet = new HashSet<>(userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден")).getFriends());
        Set<Long> otherUserFriendsSet = new HashSet<>(userStorage.findById(otherId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + otherId + " не найден")).getFriends());
        Set<Long> common = new HashSet<>(userFriendsSet);
        for (Long friendId : userFriendsSet) {
            if (otherUserFriendsSet.contains(friendId)) {
                common.add(friendId);
            }
        }
        List<User> commonFriendsList = new ArrayList<>();
        for (Long id : common) {
            commonFriendsList.add(userStorage.findById(id).orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден")));
        }

        //Set<Long> common = userFriendsSet.retainAll(otherUserFriendsSet);



        return commonFriendsList;
                //commonFriendsList; //new ArrayList<>(userFriendsSet);
    }

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

//        if (!user.getFriends().contains(friendId)) {
//            throw new NotFoundException("У пользователя " + userId + " нет друга " + friendId);
//        }

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

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
