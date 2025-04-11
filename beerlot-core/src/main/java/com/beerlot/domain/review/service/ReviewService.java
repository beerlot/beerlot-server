package com.beerlot.domain.review.service;

import com.beerlot.domain.beer.Beer;
import com.beerlot.domain.beer.repository.BeerRepository;
import com.beerlot.domain.beer.service.BeerService;
import com.beerlot.domain.common.entity.LanguageType;
import com.beerlot.domain.common.page.PageCustom;
import com.beerlot.domain.common.page.PageCustomRequest;
import com.beerlot.domain.member.Member;
import com.beerlot.domain.member.repository.MemberRepository;
import com.beerlot.domain.member.service.MemberService;
import com.beerlot.domain.review.Review;
import com.beerlot.domain.review.dto.request.ReviewRequest;
import com.beerlot.domain.review.dto.response.ReviewArchiveResponse;
import com.beerlot.domain.review.dto.response.ReviewResponse;
import com.beerlot.domain.review.repository.ReviewRepository;
import com.beerlot.exception.ConflictException;
import com.beerlot.exception.ErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;


@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {

    private final MemberService memberService;
    private final BeerService beerService;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private BeerRepository beerRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public PageCustom<ReviewResponse> findByBeerId(Long beerId, PageCustomRequest pageRequest) {
        Beer beer = beerService.findBeerById(beerId);

        PageCustom<ReviewResponse> reviewPage = reviewRepository.findByBeerId(beerId, pageRequest);

        return reviewPage;
    }

    public void createReview(String oauthId, Long beerId, ReviewRequest reviewRequest) {
        Beer beer = beerService.findBeerById(beerId);
        Member member = memberService.findMemberByOauthId(oauthId);

        if (reviewRepository.findByBeer_IdAndMember_Id(beer.getId(), member.getId()).isPresent()) {
            throw new ConflictException(ErrorMessage.REVIEW__ALREADY_EXIST.getMessage());
        }

        Review review = ReviewRequest.to(reviewRequest, beer, member);

        beer.addReview();
        beer.calculateRate(review.getRate());
        beer.addBuyFrom(review.getBuyFrom());

        reviewRepository.save(review);
    }

    public ReviewResponse updateReview(String oauthId, Long reviewId, ReviewRequest reviewRequest) {
        Review review = findById(reviewId);

        isReviewOwner(oauthId, review);

        if (!isBuyFromShared(review.getBuyFrom(), review.getId())) {
            review.getBeer().removeBuyFrom(review.getBuyFrom());
        }

        review.update(reviewRequest);
        review.getBeer().calculateRate(review.getRate());
        if (reviewRequest.getBuyFrom() != null) {
            review.getBeer().addBuyFrom(review.getBuyFrom());
        }

        return ReviewResponse.of(review);
    }

    public void deleteReview(String oauthId, Long reviewId) {
        Review review = findById(reviewId);

        isReviewOwner(oauthId, review);

        review.getBeer().removeReview();
        review.getBeer().calculateRate(0 - review.getRate());
        if (!isBuyFromShared(review.getBuyFrom(), review.getId())) {
            review.getBeer().removeBuyFrom(review.getBuyFrom());
        }

        reviewRepository.delete(review);
    }

    @Transactional(readOnly = true)
    public PageCustom<ReviewResponse> findAllReviews(PageCustomRequest pageRequest, LanguageType language) {
        return reviewRepository.findAll(pageRequest, language);
    }

    @Transactional
    public Review findById(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NoSuchElementException(ErrorMessage.REVIEW__NOT_FOUND.getMessage()));
    }

    @Transactional(readOnly = true)
    public ReviewResponse findByReviewIdAndLanguage(Long reviewId, LanguageType language) {
        return reviewRepository.findByReviewId(reviewId, language);
    }

    @Transactional(readOnly = true)
    public PageCustom<ReviewArchiveResponse> getReviewsByMember(String oauthId,
                                                                PageCustomRequest pageRequest,
                                                                LanguageType language) {
        return reviewRepository.findByMember(oauthId, pageRequest, language);
    }

    @Transactional(readOnly = true)
    public ReviewResponse getReviewByMemberAndBeerID(String oauthId, Long beerId) {
        Beer beer = beerService.findBeerById(beerId);
        Member member = memberService.findMemberByOauthId(oauthId);

        Optional<Review> maybeReview = reviewRepository.findByBeer_IdAndMember_Id(beer.getId(), member.getId());
        if (maybeReview.isEmpty()) {
            throw new IllegalArgumentException(ErrorMessage.REVIEW__NOT_FOUND.getMessage());
        }

        return ReviewResponse.of(maybeReview.get());
    }

    @Transactional(readOnly = true)
    private void isReviewOwner(String oauthId, Review review) {
        if (!review.getMember().getOauthId().equals(oauthId)) {
            throw new AccessDeniedException(ErrorMessage.MEMBER__ACCESS_DENIED.getMessage());
        }
    }

    @Transactional(readOnly = true)
    private boolean isBuyFromShared(String buyFrom, Long reviewId) {
        return reviewRepository.existsByIdNotAndBuyFrom(reviewId, buyFrom);
    }
}
