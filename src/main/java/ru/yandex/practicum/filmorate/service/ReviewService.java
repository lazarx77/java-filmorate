package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.util.List;

/**
 * Сервисный класс для управления отзывами на фильмы.
 * Предоставляет методы для создания, обновления, удаления и получения отзывов,
 * а также для управления лайками и дизлайками к отзывам.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewStorage reviewDbStorage;
    private final UserDbService userDbService;
    private final FilmDbService filmDbService;

    /**
     * Получает отзыв по его идентификатору.
     *
     * @param id идентификатор отзыва
     * @return отзыв, соответствующий указанному идентификатору
     * @throws NotFoundException если отзыв с таким идентификатором не найден
     */
    public Review getReview(Long id) {
        return reviewDbStorage.getReview(id);
    }

    /**
     * Получает список отзывов для указанного фильма.
     *
     * @param filmId идентификатор фильма
     * @param count  количество отзывов, которые необходимо вернуть
     * @return список отзывов для указанного фильма
     * @throws NotFoundException если фильм с таким идентификатором не найден
     */
    public List<Review> getReviewsForFilm(Long filmId, Integer count) {
        return reviewDbStorage.getReviewsForFilm(filmId, count);
    }

    /**
     * Добавляет новый отзыв.
     *
     * @param review объект, который необходимо добавить
     * @return добавленный отзыв
     * @throws NotFoundException если идентификаторы пользователя, фильма, отзыва отрицательные не найдены
     */
    public Review addReview(Review review) {
        if (review.getUserId() <= 0 || review.getFilmId() <= 0) {
            throw new NotFoundException("ID must be positive");
        }
        filmDbService.findById(review.getFilmId());
        userDbService.findById(review.getUserId());
        return reviewDbStorage.addReview(review);
    }

    /**
     * Обновляет существующий отзыв.
     *
     * @param review объект {@link Review}, содержащий обновленные данные
     * @return обновленный объект {@link Review}
     * @throws NotFoundException если идентификаторы пользователя, фильма, отзыва отрицательные не найдены
     */
    public Review updateReview(Review review) {
        if (review.getUserId() <= 0 || review.getFilmId() <= 0) {
            throw new NotFoundException("ID must be positive");
        }
        reviewDbStorage.getReview(review.getReviewId());
        filmDbService.findById(review.getFilmId());
        userDbService.findById(review.getUserId());
        return reviewDbStorage.updateReview(review);
    }

    /**
     * Удаляет отзыв по его идентификатору.
     *
     * @param id идентификатор отзыва, который необходимо удалить
     * @throws NotFoundException если отзыв с таким идентификатором не найден
     */
    public void deleteReview(Long id) {
        reviewDbStorage.getReview(id);
        reviewDbStorage.deleteReview(id);
    }

    /**
     * Добавляет лайк к отзыву.
     *
     * @param reviewId идентификатор отзыва, к которому добавляется лайк
     * @param userId   идентификатор пользователя, который ставит лайк
     * @throws NotFoundException если отзыв или пользователь с указанными идентификаторами не найдены
     */
    public void addLikeInReview(Long reviewId, Long userId) {
        checkId(reviewId, userId);
        reviewDbStorage.addLikeInReview(reviewId, userId);
    }

    /**
     * Добавляет дизлайк к отзыву.
     *
     * @param reviewId идентификатор отзыва, к которому добавляется дизлайк
     * @param userId   идентификатор пользователя, который ставит дизлайк
     * @throws NotFoundException если отзыв или пользователь с указанными идентификаторами не найдены
     */
    public void addDislikeInReview(Long reviewId, Long userId) {
        checkId(reviewId, userId);
        reviewDbStorage.addDislikeInReview(reviewId, userId);
    }

    /**
     * Удаляет лайк с отзыва.
     *
     * @param reviewId идентификатор отзыва, с которого удаляется лайк
     * @param userId   идентификатор пользователя, который удаляет лайк
     * @throws NotFoundException если отзыв или пользователь с указанными идентификаторами не найдены
     */
    public void deleteLikeInReview(Long reviewId, Long userId) {
        checkId(reviewId, userId);
        reviewDbStorage.deleteLikeInReview(reviewId, userId);
    }

    /**
     * Удаляет дизлайк с отзыва.
     *
     * @param reviewId идентификатор отзыва, с которого удаляется дизлайк
     * @param userId   идентификатор пользователя, который удаляет дизлайк
     * @throws NotFoundException если отзыв или пользователь с указанными идентификаторами не найдены
     */
    public void deleteDislikeInReview(Long reviewId, Long userId) {
        checkId(reviewId, userId);
        reviewDbStorage.deleteDislikeInReview(reviewId, userId);
    }

    /**
     * Проверяет наличие отзыва и пользователя по их идентификаторам.
     *
     * @param reviewId идентификатор отзыва
     * @param userId   идентификатор пользователя
     * @throws NotFoundException если отзыв или пользователь с указанными идентификаторами не найдены
     */
    private void checkId(Long reviewId, Long userId) {
        reviewDbStorage.getReview(reviewId);
        userDbService.findById(userId);
    }
}
