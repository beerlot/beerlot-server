package com.beerlot.core.domain.review.controller;

import com.beerlot.core.domain.review.ReviewSortType;
import com.beerlot.core.domain.review.dto.request.ReviewRequest;
import com.beerlot.core.domain.review.dto.response.ReviewPage;
import com.beerlot.core.domain.review.dto.response.ReviewResponse;
import com.beerlot.core.domain.review.service.ReviewLikeService;
import com.beerlot.core.domain.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class ReviewController implements ReviewApi, ReviewLikeApi {

    private final ReviewService reviewService;
    private final ReviewLikeService reviewLikeService;

    @Override
    public ResponseEntity<Void> createReview(Long beerId, ReviewRequest reviewRequest) {
        reviewService.createReview(beerId, reviewRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<ReviewResponse> updateReview(Long reviewId, ReviewRequest reviewRequest) {
        return new ResponseEntity<>(reviewService.updateReview(reviewId, reviewRequest), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> deleteReview(Long reviewId) {
        reviewService.deleteReview(reviewId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<ReviewResponse> findReviewById(Long reviewId) {
        return new ResponseEntity<>(reviewService.findById(reviewId), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ReviewPage> findReviewsByBeerId(Long beerId, Integer page, Integer size, ReviewSortType sort) {
        return new ResponseEntity<>(reviewService.findByBeerId(beerId, page, size, sort), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ReviewPage> findAllReviews(Integer page, Integer size, ReviewSortType sort) {
        try {
            return new ResponseEntity<>(reviewService.findAllReviews(page, size, sort), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<Void> createReviewLike(Long reviewId) {
        reviewLikeService.likeReview(reviewId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> deleteReviewLike(Long reviewId) {
        reviewLikeService.unlikeReview(reviewId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
