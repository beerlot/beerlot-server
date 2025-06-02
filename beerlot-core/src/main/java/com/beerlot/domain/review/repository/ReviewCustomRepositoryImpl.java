package com.beerlot.domain.review.repository;

import com.beerlot.domain.beer.QBeer;
import com.beerlot.domain.common.entity.LanguageType;
import com.beerlot.domain.common.page.PageCustom;
import com.beerlot.domain.common.page.PageCustomImpl;
import com.beerlot.domain.common.page.PageCustomRequest;
import com.beerlot.domain.member.QMember;
import com.beerlot.domain.member.dto.response.MemberResponse;
import com.beerlot.domain.review.QReview;
import com.beerlot.domain.review.dto.response.ReviewArchiveResponse;
import com.beerlot.domain.review.dto.response.ReviewResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.beerlot.domain.beer.QBeer.beer;
import static com.beerlot.domain.beer.QBeerInternational.beerInternational;
import static com.beerlot.domain.member.QMember.member;
import static com.beerlot.domain.review.QReview.review;

@Repository
@RequiredArgsConstructor
public class ReviewCustomRepositoryImpl implements ReviewCustomRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public PageCustom<ReviewResponse> findByBeerId(Long beerId, PageCustomRequest pageRequest) {
        QMember member = new QMember("m");
        QBeer beer = new QBeer("b");

        JPAQuery<ReviewResponse> query = queryFactory
                .select(Projections.fields(ReviewResponse.class,
                            review.id,
                            review.content,
                            review.imageUrl,
                            review.rate,
                            review.likeCount,
                            review.updatedAt,
                            review.createdAt,
                            review.buyFrom,
                            Projections.fields(MemberResponse.class,
                                    member.id,
                                    member.username,
                                    member.imageUrl
                            ).as("member")
                        )
                )
                .from(review)
                .innerJoin(review.beer, beer)
                .innerJoin(review.member, member)
                .where(
                        beer.id.eq(beerId)
                );

        long totalElements = query.fetch().size();

        List<ReviewResponse> reviewResponseList = query
                .limit(pageRequest.getSize())
                .offset(pageRequest.getOffset())
                .orderBy(pageRequest.getSort().orderBy(QReview.class, review.getMetadata().getName()))
                .fetch();

        return new PageCustomImpl<>(reviewResponseList, pageRequest, totalElements);
    }

    @Override
    public PageCustom<ReviewResponse> findAll(PageCustomRequest pageRequest, LanguageType language) {
        JPAQuery<ReviewResponse> query = queryFactory
                .select(Projections.fields(ReviewResponse.class,
                                review.id,
                                review.content,
                                review.imageUrl,
                                review.rate,
                                review.likeCount,
                                review.updatedAt,
                                review.createdAt,
                                review.buyFrom,
                                Projections.fields(MemberResponse.class,
                                        review.member.id,
                                        review.member.username,
                                        review.member.imageUrl
                                ).as("member"),
                                Projections.fields(ReviewResponse.BeerResponse.class,
                                        review.beer.id,
                                        beerInternational.name
                                ).as("beer")
                        )
                )
                .from(review)
                .innerJoin(review.beer, beer)
                .innerJoin(review.beer.beerInternationals, beerInternational)
                .where(
                        matchLanguage(language)
                );

        long totalElements = query.fetch().size();

        List<ReviewResponse> reviewResponseList = query
                .limit(pageRequest.getSize())
                .offset(pageRequest.getOffset())
                .orderBy(pageRequest.getSort().orderBy(QReview.class, review.getMetadata().getName()))
                .fetch();

        return new PageCustomImpl<>(reviewResponseList, pageRequest, totalElements);
    }

    @Override
    public PageCustom<ReviewArchiveResponse> findByMember(String oauthId,
                                                          PageCustomRequest pageRequest,
                                                          LanguageType language) {
        JPAQuery<ReviewArchiveResponse> query = queryFactory
                .select(Projections.fields(ReviewArchiveResponse.class,
                                review.id,
                                review.content,
                                review.imageUrl,
                                review.rate,
                                review.likeCount,
                                review.createdAt,
                                review.updatedAt,
                                Projections.fields(ReviewArchiveResponse.BeerResponse.class,
                                        beer.id,
                                        beerInternational.name,
                                        beer.imageUrl
                                        ).as("beer")
                        )
                )
                .from(review)
                .innerJoin(review.member, member)
                .innerJoin(review.beer, beer)
                .innerJoin(beer.beerInternationals, beerInternational)
                .where(
                        matchLanguage(language),
                        matchOauthId(oauthId)
                );

        long totalElements = query.fetch().size();

        List<ReviewArchiveResponse> reviewResponseList = query
                .limit(pageRequest.getSize())
                .offset(pageRequest.getOffset())
                .orderBy(pageRequest.getSort().orderBy(QReview.class, review.getMetadata().getName()))
                .fetch();

        return new PageCustomImpl<>(reviewResponseList, pageRequest, totalElements);
    }

    @Override
    public ReviewResponse findByReviewId(Long reviewId, LanguageType language) {
        JPAQuery<ReviewResponse> query = queryFactory
                .select(Projections.fields(ReviewResponse.class,
                                review.id,
                                review.content,
                                review.imageUrl,
                                review.rate,
                                review.likeCount,
                                review.updatedAt,
                                review.createdAt,
                                review.buyFrom,
                                Projections.fields(ReviewResponse.BeerResponse.class,
                                        review.beer.id,
                                        beerInternational.name
                                ).as("beer")
                        )
                )
                .from(review)
                .innerJoin(review.beer, beer)
                .innerJoin(review.beer.beerInternationals, beerInternational)
                .where(
                        matchReviewId(reviewId),
                        matchLanguage(language)
                );

        return query.fetchFirst();
    }
  
    private BooleanExpression matchLanguage(LanguageType language) {
        return beerInternational.id.language.stringValue().eq(language.toString());
    }

    private BooleanExpression matchReviewId(Long reviewId) {
        return review.id.eq(reviewId);
    }

    private BooleanExpression matchOauthId(String oauthId) {
        return member.oauthId.eq(oauthId);
    }
}
