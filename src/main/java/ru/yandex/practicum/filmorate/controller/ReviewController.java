package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

/**
 * Контроллер для обработки HTTP-запросов, связанных с отзывами о фильмах.
 * Обрабатывает запросы на получение, добавление, обновление, удаление отзывов,
 * а также на управление лайками и дизлайками.
 */
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * Возвращает отзыв по его идентификатору.
     *
     * @param id идентификатор отзыва
     * @return объект Review, соответствующий переданному идентификатору
     */
    @GetMapping("/{id}")
    public Review getReview(@PathVariable Long id) {
        return reviewService.getReview(id);
    }

    /**
     * Возвращает список отзывов для определенного фильма.
     * Если идентификатор фильма не указан, возвращаются все отзывы.
     * Если указано количество отзывов, возвращается указанное количество самых полезных отзывов.
     *
     * @param filmId идентификатор фильма (необязательный)
     * @param count  количество отзывов, которые необходимо вернуть (необязательный)
     * @return список отзывов
     */
    @GetMapping
    public List<Review> getReviewsForFilm(@RequestParam(required = false) Long filmId,
                                          @RequestParam(required = false) Integer count) {
        return reviewService.getReviewsForFilm(filmId, count);
    }

    /**
     * Добавляет новый отзыв.
     *
     * @param review объект Review, содержащий данные нового отзыва
     * @return объект Review, содержащий данные добавленного отзыва
     */
    @PostMapping
    public Review addReview(@Valid @RequestBody Review review) {
        return reviewService.addReview(review);
    }

    /**
     * Обновляет существующий отзыв.
     *
     * @param review объект Review, содержащий обновленные данные отзыва
     * @return объект Review, содержащий данные обновленного отзыва
     */
    @PutMapping
    public Review updateReview(@Valid @RequestBody Review review) {
        return reviewService.updateReview(review);
    }

    /**
     * Удаляет отзыв по его идентификатору.
     *
     * @param id идентификатор отзыва
     */
    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
    }

    /**
     * Добавляет лайк к отзыву.
     *
     * @param reviewId идентификатор отзыва
     * @param userId   идентификатор пользователя, который ставит лайк
     */
    @PutMapping("/{id}/like/{userId}")
    public void addLikeInReview(@PathVariable("id") Long reviewId, @PathVariable Long userId) {
        reviewService.addLikeInReview(reviewId, userId);
    }

    /**
     * Добавляет дизлайк к отзыву.
     *
     * @param reviewId идентификатор отзыва
     * @param userId   идентификатор пользователя, который ставит дизлайк
     */
    @PutMapping("/{id}/dislike/{userId}")
    public void addDislikeInReview(@PathVariable("id") Long reviewId, @PathVariable Long userId) {
        reviewService.addDislikeInReview(reviewId, userId);
    }

    /**
     * Удаляет лайк с отзыва.
     *
     * @param reviewId идентификатор отзыва
     * @param userId   идентификатор пользователя, который удаляет лайк
     */
    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLikeInReview(@PathVariable("id") Long reviewId, @PathVariable Long userId) {
        reviewService.deleteLikeInReview(reviewId, userId);
    }

    /**
     * Удаляет дизлайк с отзыва.
     *
     * @param reviewId идентификатор отзыва
     * @param userId   идентификатор пользователя, который удаляет дизлайк
     */
    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislikeInReview(@PathVariable("id") Long reviewId, @PathVariable Long userId) {
        reviewService.deleteDislikeInReview(reviewId, userId);
    }
}