package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.HistoryDbStorage;
import ru.yandex.practicum.filmorate.dal.UserDbStorage;
import ru.yandex.practicum.filmorate.model.Event;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final HistoryDbStorage historyDbStorage;
    private final UserDbStorage userDbStorage;

    public List<Event> getFeed(long userId) {
        userDbStorage.getUserById(userId);
        return new ArrayList<>(historyDbStorage.getEventsByUser(userId));
    }
}
