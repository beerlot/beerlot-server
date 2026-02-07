package com.beerlot.domain.beer.repository;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.beerlot.domain.beer.QBeer.*;
import static com.beerlot.domain.beer.QBeerLike.*;
import static com.beerlot.domain.review.QReview.*;

@Repository
@RequiredArgsConstructor
public class CustomBeerRecommendRepositoryImpl implements BeerRecommendRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Long> getRecommendBeer(Map<Long, Long> elements, String oauthId) {
        List<Long> result = new ArrayList<>();

        for (Map.Entry<Long, Long> element : elements.entrySet()) {
            result.addAll(getQuery(element, oauthId));
        }

        if (result.size() < 10) {
            List<Long> famousBeerDesc = getFamousBeerDesc(10 - result.size(), result);
            result.addAll(famousBeerDesc);
        }

        return result.subList(0, 10);
    }

    private List<Long> getQuery(Map.Entry<Long, Long> element, String oauthId) {
        return queryFactory.select(beer.id)
                .from(beer)
                .where(beer.category.id.eq(element.getKey()))
                .where(beer.id.notIn(
                        JPAExpressions.select(beerLike.beer.id)
                                .from(beerLike)
                                .where(beerLike.member.oauthId.eq(oauthId))))
                .where(beer.id.notIn(
                        JPAExpressions.select(review.beer.id)
                                .from(review)
                                .where(review.member.oauthId.eq(oauthId))
                ))
                .orderBy(beer.rate.desc())
                .limit(element.getValue())
                .fetch();
    }

    private List<Long> getFamousBeerDesc(int limitCount, List<Long> excluded) {
        return queryFactory.select(beer.id)
                .from(beer)
                .where(beer.id.notIn(excluded))
                .orderBy(beer.rate.desc())
                .limit(limitCount)
                .fetch();
    }
}
