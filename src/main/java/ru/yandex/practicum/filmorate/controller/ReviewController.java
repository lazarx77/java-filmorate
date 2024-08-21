package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/{id}")
    public Review getReview(@PathVariable Long id) {
        return reviewService.getReview(id);
    }

    @GetMapping
    public List<Review> getReviewsForFilm(@RequestParam(required = false) Long filmId,
                                          @RequestParam(required = false) Integer count) {
        return reviewService.getReviewsForFilm(filmId, count);
    }

    @PostMapping
    public Review addReview(@Valid @RequestBody Review review) {
        return reviewService.addReview(review);
    }

    @PutMapping
    public Review updateReview(@Valid @RequestBody Review review) {
        return reviewService.updateReview(review);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLikeInReview(@PathVariable("id") Long reviewId, @PathVariable Long userId) {
        reviewService.addLikeInReview(reviewId, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislikeInReview(@PathVariable("id") Long reviewId, @PathVariable Long userId) {
        reviewService.addDislikeInReview(reviewId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLikeInReview(@PathVariable("id") Long reviewId, @PathVariable Long userId) {
        reviewService.deleteLikeInReview(reviewId, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislikeInReview(@PathVariable("id") Long reviewId, @PathVariable Long userId) {
        reviewService.deleteDislikeInReview(reviewId, userId);
    }
}