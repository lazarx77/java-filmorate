package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.HistoryDbStorage;
import ru.yandex.practicum.filmorate.dal.UserDbStorage;
import ru.yandex.practicum.filmorate.model.Event;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final HistoryDbStorage historyDbStorage;
    private final UserDbStorage userDbStorage;

    public List<Event> getFeed(long userId) {
        List<Event> events = new ArrayList<>();
        Set<Long> friends = userDbStorage.getUserById(userId).getFriends();
        //return friends.stream()
        //        .map(historyDbStorage::getEventsByUser)
        //        .flatMap(Collection::stream)
        //        .sorted(Comparator.comparingLong(event -> event.getTimestamp().getTime()))
       //         .collect(Collectors.toList());
        return new ArrayList<>(historyDbStorage.getEventsByUser(userId));
    }
}
