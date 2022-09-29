package com.beerlot.core.domain.review.service;

import com.beerlot.api.generated.model.ReviewCreateRequest;
import com.beerlot.api.generated.model.ReviewResponse;
import com.beerlot.api.generated.model.ReviewUpdateRequest;
import com.beerlot.core.domain.beer.Beer;
import com.beerlot.core.domain.beer.repository.BeerRepository;
import com.beerlot.core.domain.common.page.PageCustomRequest;
import com.beerlot.core.domain.member.repository.MemberRepository;
import com.beerlot.core.domain.review.Review;
import com.beerlot.core.domain.review.repository.ReviewRepository;
import com.beerlot.core.domain.review.util.ReviewResponseHelper;
import com.beerlot.core.domain.review.util.page.ReviewPage;
import com.beerlot.core.domain.review.util.sort.ReviewSortType;
import com.beerlot.core.exception.ErrorCode;
import com.beerlot.core.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private BeerRepository beerRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public ReviewResponse findById(Long reviewId) {
        checkReviewExist(reviewId);
        return ReviewResponseHelper.of(reviewRepository.findById(reviewId).get());
    }

    @Transactional(readOnly = true)
    public ReviewPage findByBeerId(Long beerId, Integer page, Integer size, ReviewSortType sort) {
        checkBeerExist(beerId);
        PageCustomRequest pageRequest = new PageCustomRequest(page, size, sort);
        Page<Review> reviewPage = reviewRepository.findByBeer_Id(beerId, (Pageable) PageRequest.of(page-1, size, sort.sortBy(true)));
        List<ReviewResponse> reviewResponseList = reviewPage.getContent().stream().map(ReviewResponseHelper::of).collect(Collectors.toList());
        return new ReviewPage(reviewResponseList, pageRequest, reviewPage.getTotalElements());
    }

    public void createReview(Long beerId, ReviewCreateRequest reviewCreateRequest) {
        checkBeerExist(beerId);
        Beer beer = beerRepository.findById(beerId).get();
        Review review = Review.builder()
                .content(reviewCreateRequest.getContent())
                .rate(reviewCreateRequest.getRate())
                .imageUrl(reviewCreateRequest.getImageUrl())
                .beer(beer)
                .member(memberRepository.findById(1L).get())
                .build();
        beer.addReview();
        beer.calculateRate(review.getRate());
        reviewRepository.save(review);
    }

    public ReviewResponse updateReview(Long reviewId, ReviewUpdateRequest reviewUpdateRequest) {
        checkReviewExist(reviewId);
        Review review = reviewRepository.findById(reviewId).get();
        review.updateModel(reviewUpdateRequest);
        review.getBeer().calculateRate(review.getRate());
        return ReviewResponseHelper.of(review);
    }

    public void deleteReview(Long reviewId) {
        checkReviewExist(reviewId);
        Review review = reviewRepository.findById(reviewId).get();
        review.getBeer().removeReview();
        review.getBeer().calculateRate(0 - review.getRate());
        reviewRepository.delete(review);
    }

    public ReviewPage findAllReviews(Integer page, Integer size, ReviewSortType sort) {
        PageCustomRequest pageRequest = new PageCustomRequest(page, size, sort);
        Page<Review> reviewPage = reviewRepository.findAll((Pageable) PageRequest.of(page-1, size, sort.sortBy(true)));
        List<ReviewResponse> reviewResponseList = reviewPage.getContent().stream().map(ReviewResponseHelper::of).collect(Collectors.toList());
        return new ReviewPage(reviewResponseList, pageRequest, reviewPage.getTotalElements());
    }

    private void checkBeerExist(Long beerId) {
        if (!beerRepository.existsById(beerId)) {
            throw new NotFoundException(ErrorCode.BEER_NOT_FOUND);
        }
    }

    private void checkReviewExist(Long reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new NotFoundException(ErrorCode.REVIEW_NOT_FOUND);
        }
    }
}
