package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

@Component
public interface ReviewStorage {

    Review getReview(Long id);

    List<Review> getReviewsForFilm(Long filmId, Integer count);

    Review addReview(Review review);

    Review updateReview(Review review);

    void deleteReview(Long id);

    void addLikeInReview(Long reviewId, Long userId);

    void addDislikeInReview(Long reviewId, Long userId);

    void deleteLikeInReview(Long reviewId, Long userId);

    void deleteDislikeInReview(Long reviewId, Long userId);
}
