package com.beerlot.domain.review;

import com.beerlot.domain.auth.security.oauth.entity.OAuthUserPrincipal;
import com.beerlot.domain.common.entity.LanguageType;
import com.beerlot.domain.common.page.PageCustom;
import com.beerlot.domain.common.page.PageCustomRequest;
import com.beerlot.domain.review.dto.request.ReviewRequest;
import com.beerlot.domain.review.dto.response.ReviewResponse;
import com.beerlot.domain.review.service.ReviewLikeService;
import com.beerlot.domain.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReviewController implements ReviewApi, ReviewLikeApi {

    private final ReviewService reviewService;
    private final ReviewLikeService reviewLikeService;

    @Override
    public ResponseEntity<Void> createReview(OAuthUserPrincipal userPrincipal, Long beerId, ReviewRequest reviewRequest) {
        reviewService.createReview(userPrincipal.getOauthId(), beerId, reviewRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<ReviewResponse> updateReview(OAuthUserPrincipal userPrincipal, Long reviewId, ReviewRequest reviewRequest) {
        return new ResponseEntity<>(reviewService.updateReview(userPrincipal.getOauthId(), reviewId, reviewRequest), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> deleteReview(OAuthUserPrincipal userPrincipal, Long reviewId) {
        reviewService.deleteReview(userPrincipal.getOauthId(), reviewId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<ReviewResponse> findReviewById(Long reviewId, LanguageType language) {
        LanguageType.validate(language);
        return new ResponseEntity<>(reviewService.findByReviewIdAndLanguage(reviewId, language), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<PageCustom<ReviewResponse>> findReviewsByBeerId(Long beerId, Integer page, Integer size, ReviewSortType sort) {
        return new ResponseEntity<>(reviewService.findByBeerId(beerId, new PageCustomRequest(page, size, sort)), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ReviewResponse> findMyReviewByBeerId(OAuthUserPrincipal userPrincipal, Long beerId) {
        return new ResponseEntity<>(reviewService.getReviewByMemberAndBeerID(userPrincipal.getOauthId(), beerId), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<PageCustom<ReviewResponse>> findAllReviews(Integer page, Integer size, ReviewSortType sort, LanguageType language) {
        LanguageType.validate(language);
        try {
            return new ResponseEntity<>(reviewService.findAllReviews(new PageCustomRequest(page, size, sort), language), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<Void> createReviewLike(OAuthUserPrincipal userPrincipal, Long reviewId) {
        reviewLikeService.likeReview(userPrincipal.getOauthId(), reviewId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> deleteReviewLike(OAuthUserPrincipal userPrincipal, Long reviewId) {
        reviewLikeService.unlikeReview(userPrincipal.getOauthId(), reviewId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
