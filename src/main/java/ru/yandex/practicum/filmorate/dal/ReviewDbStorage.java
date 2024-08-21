package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class ReviewDbStorage extends BaseRepository<Review> implements ReviewStorage {

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


    public ReviewDbStorage(JdbcTemplate jdbc, RowMapper<Review> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Review getReview(Long id) {
        log.info("Получение отзыва по ИД {}", id);
        Optional<Review> optionalReview = findOne(GET_REVIEW_QUERY, id);
        return optionalReview.orElseThrow(() -> new NotFoundException("Отзыв не найден"));
    }

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
        return review;
    }

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
        return review;
    }

    @Override
    public void deleteReview(Long id) {
        log.info("Удаление отзыва: {}", id);
        delete(DELETE_REVIEW_QUERY, id);
        log.info("Отзыв удален: {}", id);
    }

    private void increaseToUseful(Long reviewId) {
        log.info("Повышение рейтинга полезности отзыва");
        update(UPDATE_USEFUL_UP_QUERY, reviewId);
    }

    private void downgradeToUseful(Long reviewId) {
        log.info("Понижение рейтинга полезности отзыва");
        update(UPDATE_USEFUL_DOWN_QUERY, reviewId);
    }

    private boolean isLikeOrDislike(Long reviewId, Long userId) {
        Optional<String> like = findOneInstances(FIND_LIKE_OR_DISLIKE_QUERY, reviewId, userId);
        return like.isPresent();
    }

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

    @Override
    public void deleteLikeInReview(Long reviewId, Long userId) {
        log.info("Удаление лайка отзыва с ИД: {}", reviewId);
        deleteByTwoIdsAndLike(DELETE_LIKE_OR_DISLIKE_IN_REVIEW_QUERY, reviewId, userId, LIKE);
        log.info("Лайк удалён с отзыва ИД: {}", reviewId);
        downgradeToUseful(reviewId);
    }

    @Override
    public void deleteDislikeInReview(Long reviewId, Long userId) {
        log.info("Удаление дислайка отзыва с ИД: {}", reviewId);
        deleteByTwoIdsAndLike(DELETE_LIKE_OR_DISLIKE_IN_REVIEW_QUERY, reviewId, userId, DISLIKE);
        log.info("Дислайк удалён с отзыва ИД: {}", reviewId);
        increaseToUseful(reviewId);
    }
}