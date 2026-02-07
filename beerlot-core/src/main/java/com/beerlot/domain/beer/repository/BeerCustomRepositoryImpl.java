package com.beerlot.domain.beer.repository;

import com.beerlot.domain.beer.QBeer;
import com.beerlot.domain.beer.dto.response.BeerSimpleResponse;
import com.beerlot.domain.category.dto.response.CategorySimpleResponse;
import com.beerlot.domain.common.entity.LanguageType;
import com.beerlot.domain.common.page.PageCustom;
import com.beerlot.domain.common.page.PageCustomImpl;
import com.beerlot.domain.common.page.PageCustomRequest;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.beerlot.domain.beer.QBeer.beer;
import static com.beerlot.domain.beer.QBeerInternational.beerInternational;
import static com.beerlot.domain.beer.QBeerLike.beerLike;
import static com.beerlot.domain.category.QCategoryInternational.categoryInternational;
import static com.beerlot.domain.member.QMember.member;

@Repository
@RequiredArgsConstructor
public class BeerCustomRepositoryImpl implements BeerCustomRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public PageCustom<BeerSimpleResponse> findBySearch (String keyword,
                                                        List<Long> categories,
                                                        List<String> countries,
                                                        Integer volumeMin,
                                                        Integer volumeMax,
                                                        LanguageType language, PageCustomRequest pageRequest) {
        JPAQuery<BeerSimpleResponse> query = queryFactory
                .select(Projections.fields(BeerSimpleResponse.class,
                        beer.id,
                        beerInternational.name,
                        beerInternational.originCountry,
                        beer.imageUrl,
                        Projections.fields(CategorySimpleResponse.class,
                                    beer.category.id,
                                    categoryInternational.name
                                ).as("category")
                        ))
                .from(beer)
                .innerJoin(beer.beerInternationals, beerInternational)
                .innerJoin(beer.category.categoryInternationals, categoryInternational)
                .where(
                        matchLanguage(language),
                        hasKeyword(keyword),
                        hasCategories(categories),
                        hasCountries(countries),
                        betweenVolumes(volumeMin, volumeMax)
                );

        long totalElements = query.fetch().size();

        List<BeerSimpleResponse> beerResponseList = query
                .limit(pageRequest.getSize())
                .offset(pageRequest.getOffset())
                .orderBy(pageRequest.getSort().orderBy(QBeer.class, beer.getMetadata().getName()))
                .fetch();

        return new PageCustomImpl<>(beerResponseList, pageRequest, totalElements);
    }

    @Override
    public PageCustom<BeerSimpleResponse> findByMember(String oauthId,
                                                       PageCustomRequest pageRequest,
                                                       LanguageType language) {
        JPAQuery<BeerSimpleResponse> query = queryFactory
                .select(Projections.fields(BeerSimpleResponse.class,
                        beer.id,
                        beerInternational.name,
                        beerInternational.originCountry,
                        beer.imageUrl,
                        Projections.fields(CategorySimpleResponse.class,
                                beer.category.id,
                                categoryInternational.name
                        ).as("category")
                ))
                .from(beer)
                .innerJoin(beer.beerLikes, beerLike)
                .innerJoin(beerLike.member, member)
                .innerJoin(beer.beerInternationals, beerInternational)
                .innerJoin(beer.category.categoryInternationals, categoryInternational)
                .where(
                        matchLanguage(language),
                        matchOauthId(oauthId)
                );

        long totalElements = query.fetch().size();

        List<BeerSimpleResponse> beerResponseList = query
                .limit(pageRequest.getSize())
                .offset(pageRequest.getOffset())
                .orderBy(pageRequest.getSort().orderBy(QBeer.class, beer.getMetadata().getName()))
                .fetch();

        return new PageCustomImpl<>(beerResponseList, pageRequest, totalElements);
    }

    private BooleanExpression matchOauthId(String oauthId) {
        return beerLike.member.oauthId.eq(oauthId);
    }

    private BooleanExpression matchLanguage(LanguageType language) {
        return beerInternational.id.language.stringValue().eq(language.toString())
                .and(categoryInternational.id.language.stringValue().eq(language.toString()));
    }

    private BooleanExpression hasKeyword(String keyword) {
        if (StringUtils.isNullOrEmpty(keyword)) {
            return null;
        }
        return beerInternational.name.contains(keyword)
                .or(beerInternational.description.contains(keyword))
                .or(beerInternational.originCountry.contains(keyword))
                .or(categoryInternational.name.contains(keyword))
                .or(categoryInternational.description.contains(keyword));
    }

    private BooleanExpression hasCategories(List<Long> categoryIds) {
        if (categoryIds == null) return null;
        return beer.category.id.in(categoryIds);
    }

    private BooleanExpression hasCountries(List<String> countries) {
        if (countries == null) return null;
        return beerInternational.originCountry.in(countries);
    }

    private BooleanExpression betweenVolumes(Integer volumeMin, Integer volumeMax) {
        if (volumeMin == null && volumeMax == null) return null;
        Integer _volumeMin = volumeMin == null ? 0 : volumeMin;
        Integer _volumeMax = volumeMax == null ? 100 : volumeMax;
        return beer.volume.castToNum(Integer.class).between(_volumeMin, _volumeMax);
    }
}
