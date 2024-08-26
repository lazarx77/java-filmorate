# java-filmorate
Filmorate project by The First Group - Sprint 13
by:
Barinov Rodion,
Galkin Anton,
Korsakov Alexander,
Lazarenko Oleg (team lead),
Stepanov Dmitry.

![Database project.](/filmorate.png)

# Filmorate

Filmorate - это сервис, который позволяет пользователям оценивать фильмы и делиться своими оценками с другими пользователями.

## Функциональность

- Регистрация и аутентификация пользователей
- Добавление, редактирование и удаление фильмов
- Оценка фильмов пользователями
- Получение списка популярных фильмов
- Получение списка друзей пользователя
- Добавление и удаление друзей
- Получение списка общих друзей пользователей
- Получение списка рекомендаций фильмов для пользователя и др.

## Обработка запросов

- Получение всех фильмов: SELECT * FROM films
- Получение всех пользователей: SELECT * FROM users
- Получение всех жанров: SELECT * FROM genres ORDER BY id
- Получение всех значений рейтинга: SELECT * FROM rating ORDER BY id 
- Получение всех лайков: SELECT * FROM likes ORDER BY id
- Получение всех друзей: SELECT * FROM friends ORDER BY id
- Получение всех рейтингов: SELECT * FROM ratings ORDER BY id
- Получение всех жанров: SELECT * FROM genres ORDER BY id
- Получение всех фильмов с лайками: SELECT * FROM films_likes ORDER BY id
- Получение всех фильмов с жанрами: SELECT * FROM films_genres ORDER BY id