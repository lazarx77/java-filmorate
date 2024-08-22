package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.enums.EventTypes;
import ru.yandex.practicum.filmorate.model.enums.OperationTypes;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.util.List;
import java.util.Optional;

/**
 * Класс для работы с базой данных отзывов на фильмы.
 * Реализует интерфейс ReviewStorage и предоставляет методы для управления отзывами,
 * включая добавление, обновление, удаление, а также управление лайками и дизлайками.
 */
@Slf4j
@Repository
public class ReviewDbStorage extends BaseRepository<Review> implements ReviewStorage {
    private final HistoryDbStorage historyDbStorage;

    private static final String LIKE = "like";
    private static final String DISLIKE = "dislike";
    private static final String FIND_ALL_REVIEW_QUERY = """
            SELECT *
            FROM reviews
            ORDER BY useful DESC""";
    private static final String FIND_ALL_REVIEW_WITH_ID_QUERY = """
            SELECT *
            FROM reviews
            WHERE film_id = ?
            ORDER BY useful DESC""";
    private static final String FIND_ALL_REVIEW_WITH_COUNT_QUERY = """
            SELECT *
            FROM reviews
            WHERE film_id = ?
            ORDER BY useful DESC
            LIMIT ?""";
    private static final String GET_REVIEW_QUERY = "SELECT * FROM reviews WHERE review_id = ?";
    private static final String ADD_REVIEW_QUERY = """
            INSERT INTO reviews (content, is_positive, user_id, film_id, useful)
            VALUES (?, ?, ?, ?, ?)
            """;
    private static final String UPDATE_REVIEW_QUERY = """
            UPDATE reviews SET content = ?, is_positive = ?, user_id = ?,
            film_id = ?, useful = ?
            WHERE review_id = ?""";
    private static final String DELETE_REVIEW_QUERY = "DELETE FROM reviews WHERE review_id = ?";
    private static final String ADD_LIKE_OR_DISLIKE_IN_REVIEW_QUERY = """
            INSERT INTO reviews_users_likes (review_id, user_id, like_or_dislike)
            VALUES (?, ?, ?)
            """;
    private static final String DELETE_LIKE_OR_DISLIKE_IN_REVIEW_QUERY = """
            DELETE FROM reviews_users_likes
            WHERE review_id = ? AND user_id = ? AND like_or_dislike = ?
            """;
    private static final String UPDATE_USEFUL_UP_QUERY = """
            UPDATE reviews SET useful = useful + 1
            WHERE review_id = ?""";
    private static final String UPDATE_USEFUL_DOWN_QUERY = """
            UPDATE reviews SET useful = useful - 1
            WHERE review_id = ?""";
    private static final String FIND_LIKE_OR_DISLIKE_QUERY = """
            SELECT like_or_dislike
            FROM reviews_users_likes
            WHERE review_id = ? AND user_id = ?""";


    public ReviewDbStorage(JdbcTemplate jdbc, RowMapper<Review> mapper, HistoryDbStorage historyDbStorage) {
        super(jdbc, mapper);
        this.historyDbStorage = historyDbStorage;
    }

    /**
     * Получает отзыв по его идентификатору.
     *
     * @param id идентификатор отзыва
     * @return объект Review, соответствующий указанному идентификатору
     * @throws NotFoundException если отзыв с таким идентификатором не найден
     */
    @Override
    public Review getReview(Long id) {
        log.info("Получение отзыва по ИД {}", id);
        Optional<Review> optionalReview = findOne(GET_REVIEW_QUERY, id);
        return optionalReview.orElseThrow(() -> new NotFoundException("Отзыв не найден"));
    }

    /**
     * Получает список отзывов для указанного фильма.
     * Если filmId или count равны null, возвращаются все отзывы или ограниченное количество.
     *
     * @param filmId идентификатор фильма
     * @param count  количество отзывов, которое необходимо вернуть
     * @return список отзывов для указанного фильма
     */
    @Override
    public List<Review> getReviewsForFilm(Long filmId, Integer count) {
        if (filmId == null) {
            log.info("Получение всех отзывов");
            return findMany(FIND_ALL_REVIEW_QUERY);
        } else if (count == null) {
            log.info("Получение отзывов по ИД фильма {}", filmId);
            return findMany(FIND_ALL_REVIEW_WITH_ID_QUERY, filmId);
        } else {
            log.info("Получение отзывов по ИД фильма {} в колличестве {} шт.", filmId, count);
            return findMany(FIND_ALL_REVIEW_WITH_COUNT_QUERY, filmId, count);
        }
    }

    /**
     * Добавляет новый отзыв в базу данных.
     * Устанавливает начальное значение полезности равным 0.
     *
     * @param review объект Review, который необходимо добавить
     * @return добавленный объект Review
     */
    @Override
    public Review addReview(Review review) {
        log.info("Добавление отзыва: {}", review);
        review.setUseful(0);
        long id = insertWithGenId(
                ADD_REVIEW_QUERY,
                review.getContent(),
                review.getIsPositive(),
                review.getUserId(),
                review.getFilmId(),
                review.getUseful()
        );
        review.setReviewId(id);
        log.info("Отзыв добавлен: {}", review);
        saveHistory(review.getReviewId(), review.getUserId(), OperationTypes.ADD);
        return review;
    }

    /**
     * Обновляет существующий отзыв в базе данных.
     * Если поле полезности не установлено, используется текущее значение.
     *
     * @param review объект Review, содержащий обновленные данные
     * @return обновленный объект Review
     * @throws NotFoundException если отзыв с указанным идентификатором не найден
     */
    @Override
    public Review updateReview(Review review) {
        log.info("Обновление отзыва: {}", review);
        if (review.getUseful() == null) {
            review.setUseful(getReview(review.getReviewId()).getUseful());
        }
        update(UPDATE_REVIEW_QUERY,
                review.getContent(),
                review.getIsPositive(),
                review.getUserId(),
                review.getFilmId(),
                review.getUseful(),
                review.getReviewId()
        );
        log.info("Отзыв обновлён: {}", review);
        saveHistory(review.getReviewId(), review.getUserId(), OperationTypes.UPDATE);
        return review;
    }

    /**
     * Удаляет отзыв по его идентификатору из базы данных.
     * Записывает событие удаления в историю.
     *
     * @param id идентификатор отзыва, который необходимо удалить
     * @throws NotFoundException если отзыв с таким идентификатором не найден
     */
    @Override
    public void deleteReview(Long id) {
        log.info("Удаление отзыва: {}", id);
        saveHistory(id, getReview(id).getUserId(), OperationTypes.REMOVE);
        delete(DELETE_REVIEW_QUERY, id);
        log.info("Отзыв удален: {}", id);
    }

    /**
     * Сохраняет событие (например, добавление, обновление, удаление отзыва) в историю.
     *
     * @param id идентификатор отзыва
     * @param userId идентификатор пользователя, связанного с событием
     * @param operationTypes тип операции (добавление, обновление, удаление)
     */
    private void saveHistory(Long id, Long userId, OperationTypes operationTypes) {
        historyDbStorage.addEvent(Event.builder()
                .userId(userId)
                .timestamp(System.currentTimeMillis())
                .eventType(EventTypes.REVIEW)
                .operation(operationTypes)
                .entityId(id)
                .build());
    }

    /**
     * Повышает полезность отзыва (увеличивает значение useful).
     *
     * @param reviewId идентификатор отзыва, полезность которого необходимо повысить
     */
    private void increaseToUseful(Long reviewId) {
        log.info("Повышение рейтинга полезности отзыва");
        update(UPDATE_USEFUL_UP_QUERY, reviewId);
    }

    /**
     * Понижает полезность отзыва (уменьшает значение useful).
     *
     * @param reviewId идентификатор отзыва, полезность которого необходимо понизить
     */
    private void downgradeToUseful(Long reviewId) {
        log.info("Понижение рейтинга полезности отзыва");
        update(UPDATE_USEFUL_DOWN_QUERY, reviewId);
    }

    /**
     * Проверяет, существует ли лайк или дизлайк на отзыв от конкретного пользователя.
     *
     * @param reviewId идентификатор отзыва
     * @param userId идентификатор пользователя
     * @return true, если лайк или дизлайк существует, иначе false
     */
    private boolean isLikeOrDislike(Long reviewId, Long userId) {
        Optional<String> like = findOneInstances(FIND_LIKE_OR_DISLIKE_QUERY, reviewId, userId);
        return like.isPresent();
    }

    /**
     * Добавляет лайк на отзыв.
     * Если у пользователя уже есть дизлайк, он удаляется.
     * Увеличивает значение полезности отзыва.
     *
     * @param reviewId идентификатор отзыва
     * @param userId идентификатор пользователя
     */
    @Override
    public void addLikeInReview(Long reviewId, Long userId) {
        if (isLikeOrDislike(reviewId, userId)) {
            deleteDislikeInReview(reviewId, userId);
        }
        log.info("Добавление лайка на ИД отзыва: {}", reviewId);
        insert(ADD_LIKE_OR_DISLIKE_IN_REVIEW_QUERY, reviewId, userId, LIKE);
        log.info("Лайк добавлен на ИД отзыва: {}", reviewId);
        increaseToUseful(reviewId);
    }

    /**
     * Добавляет дизлайк на отзыв.
     * Если у пользователя уже есть лайк, он удаляется.
     * Уменьшает значение полезности отзыва.
     *
     * @param reviewId идентификатор отзыва
     * @param userId идентификатор пользователя
     */
    @Override
    public void addDislikeInReview(Long reviewId, Long userId) {
        if (isLikeOrDislike(reviewId, userId)) {
            deleteLikeInReview(reviewId, userId);
        }
        log.info("Добавление дислайка на ИД отзыва: {}", reviewId);
        insert(ADD_LIKE_OR_DISLIKE_IN_REVIEW_QUERY, reviewId, userId, DISLIKE);
        log.info("Дислайк добавлен на ИД отзыва: {}", reviewId);
        downgradeToUseful(reviewId);
    }

    /**
     * Удаляет лайк с отзыва и понижает значение полезности.
     *
     * @param reviewId идентификатор отзыва
     * @param userId идентификатор пользователя
     */
    @Override
    public void deleteLikeInReview(Long reviewId, Long userId) {
        log.info("Удаление лайка отзыва с ИД: {}", reviewId);
        deleteByTwoIdsAndLike(DELETE_LIKE_OR_DISLIKE_IN_REVIEW_QUERY, reviewId, userId, LIKE);
        log.info("Лайк удалён с отзыва ИД: {}", reviewId);
        downgradeToUseful(reviewId);
    }

    /**
     * Удаляет дизлайк с отзыва и повышает значение полезности.
     *
     * @param reviewId идентификатор отзыва
     * @param userId идентификатор пользователя
     */
    @Override
    public void deleteDislikeInReview(Long reviewId, Long userId) {
        log.info("Удаление дислайка отзыва с ИД: {}", reviewId);
        deleteByTwoIdsAndLike(DELETE_LIKE_OR_DISLIKE_IN_REVIEW_QUERY, reviewId, userId, DISLIKE);
        log.info("Дислайк удалён с отзыва ИД: {}", reviewId);
        increaseToUseful(reviewId);
    }
}