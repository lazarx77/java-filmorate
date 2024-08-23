package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

/**
 * Интерфейс для работы с хранилищем отзывов.
 * Определяет методы для получения, добавления, обновления и удаления отзывов,
 * а также для управления лайками и дизлайками к отзывам.
 */
@Component
public interface ReviewStorage {

    /**
     * Получает отзыв по его идентификатору.
     *
     * @param id идентификатор отзыва
     * @return отзыв, соответствующий указанному идентификатору
     */
    Review getReview(Long id);

    /**
     * Получает список отзывов для указанного фильма.
     *
     * @param filmId идентификатор фильма
     * @param count  количество отзывов, которые необходимо вернуть
     * @return список отзывов для указанного фильма
     */
    List<Review> getReviewsForFilm(Long filmId, Integer count);

    /**
     * Добавляет новый отзыв.
     *
     * @param review, отзыв который необходимо добавить
     * @return добавленный отзыв
     */
    Review addReview(Review review);

    /**
     * Обновляет существующий отзыв.
     *
     * @param review, содержащий обновленные данные отзыв
     * @return обновленный отзыв
     */
    Review updateReview(Review review);

    /**
     * Удаляет отзыв по его идентификатору.
     *
     * @param id идентификатор отзыва, который необходимо удалить
     */
    void deleteReview(Long id);

    /**
     * Добавляет лайк к отзыву.
     *
     * @param reviewId идентификатор отзыва, к которому добавляется лайк
     * @param userId   идентификатор пользователя, который ставит лайк
     */
    void addLikeInReview(Long reviewId, Long userId);

    /**
     * Добавляет дизлайк к отзыву.
     *
     * @param reviewId идентификатор отзыва, к которому добавляется дизлайк
     * @param userId   идентификатор пользователя, который ставит дизлайк
     */
    void addDislikeInReview(Long reviewId, Long userId);

    /**
     * Удаляет лайк с отзыва.
     *
     * @param reviewId идентификатор отзыва, с которого удаляется лайк
     * @param userId   идентификатор пользователя, который удаляет лайк
     */
    void deleteLikeInReview(Long reviewId, Long userId);

    /**
     * Удаляет дизлайк с отзыва.
     *
     * @param reviewId идентификатор отзыва, с которого удаляется дизлайк
     * @param userId   идентификатор пользователя, который удаляет дизлайк
     */
    void deleteDislikeInReview(Long reviewId, Long userId);
}
