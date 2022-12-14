package com.beerlot.domain.review.service;

import com.beerlot.domain.member.repository.MemberRepository;
import com.beerlot.domain.review.Review;
import com.beerlot.domain.review.ReviewLike;
import com.beerlot.domain.review.repository.ReviewLikeRepository;
import com.beerlot.domain.review.repository.ReviewRepository;
import com.beerlot.exception.ConflictException;
import com.beerlot.exception.ErrorMessage;
import com.beerlot.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ReviewLikeService {

    @Autowired
    private ReviewLikeRepository reviewLikeRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ReviewService reviewService;

    /* TODO: Include memberId and validateMember */
    public void likeReview(Long reviewId) {
        Review review = reviewService.findById(reviewId);
        checkReviewLikeExist(reviewId, 1L, true);
        ReviewLike reviewLike = new ReviewLike(review, memberRepository.findById(1L).get());
        reviewLikeRepository.save(reviewLike);
        review.likeReview();
    }

    /* TODO: Include memberId and validateMember */
    public void unlikeReview(Long reviewId) {
        Review review = reviewService.findById(reviewId);
        checkReviewLikeExist(reviewId, 1L, false);
        reviewLikeRepository.deleteByReview_IdAndMember_Id(reviewId, 1L);
        review.unlikeReview();
    }

    @Transactional(readOnly = true)
    private void checkReviewExist(Long reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new NotFoundException(ErrorMessage.REVIEW_NOT_FOUND);
        }
    }

    @Transactional(readOnly = true)
    private void checkReviewLikeExist(Long reviewId, Long memberId, boolean isPositive) {
        if (isPositive && reviewLikeRepository.existsByReview_IdAndMember_Id(reviewId, memberId)) {
            throw new ConflictException(ErrorMessage.REVIEW_LIKE_CONFLICT.getMessage());
        } else if (!isPositive && !reviewLikeRepository.existsByReview_IdAndMember_Id(reviewId, memberId)) {
            throw new NotFoundException(ErrorMessage.REVIEW_LIKE_NOT_FOUND);
        }
    }
}
