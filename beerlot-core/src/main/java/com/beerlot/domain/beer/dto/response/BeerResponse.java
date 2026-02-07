package com.beerlot.domain.beer.dto.response;

import com.beerlot.domain.beer.Beer;
import com.beerlot.domain.beer.BeerInternational;
import com.beerlot.domain.category.dto.response.CategorySimpleResponse;
import com.beerlot.domain.common.entity.LanguageType;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Builder;

import javax.persistence.Embedded;
import java.util.Set;

public class BeerResponse {

    @Embedded
    @JsonUnwrapped
    private BeerSimpleResponse beerSimpleResponse;

    @JsonProperty("description")
    private String description;

    @JsonProperty("brewery")
    private String brewery;

    @JsonProperty("origin_city")
    private String originCity;

    @JsonProperty("volume")
    private Float volume;

    @JsonProperty("calorie")
    private Integer calorie;

    @JsonProperty("calorie_unit")
    private Integer calorieUnit;

    @JsonProperty("buy_from")
    private Set<String> buyFrom;

    @JsonProperty("like_count")
    private Long likeCount;

    @JsonProperty("review_count")
    private Long reviewCount;

    @JsonProperty("rate")
    private Float rate;

    @Builder
    public BeerResponse(Long id,
                        String name,
                        String originCountry,
                        String imageUrl,
                        CategorySimpleResponse categorySimpleResponse,
                        String description,
                        String originCity,
                        Float volume,
                        String brewery,
                        Integer calorie,
                        Integer calorieUnit,
                        Set<String> buyFrom,
                        Long likeCount,
                        Long reviewCount,
                        Float rate
                        ) {

        this.beerSimpleResponse = BeerSimpleResponse.builder()
                .id(id)
                .name(name)
                .originCountry(originCountry)
                .imageUrl(imageUrl)
                .category(categorySimpleResponse)
                .build();

        this.description = description;
        this.originCity = originCity;
        this.volume = volume;
        this.brewery = brewery;
        this.calorie = calorie;
        this.calorieUnit = calorieUnit;
        this.buyFrom = buyFrom;
        this.likeCount = likeCount;
        this.reviewCount = reviewCount;
        this.rate = rate;
    }

    public static BeerResponse of(LanguageType language, Beer beer) {
        BeerInternational beerInternational = beer.getBeerInternationals().stream()
                .filter(bi -> bi.getId().getLanguage().equals(language))
                .findFirst().get();

        return BeerResponse.builder()
                .id(beer.getId())
                .name(beerInternational.getName())
                .description(beerInternational.getDescription())
                .originCountry(beerInternational.getOriginCountry())
                .originCity(beerInternational.getOriginCity())
                .volume(beer.getVolume())
                .brewery(beer.getBrewery().getName(language))
                .calorie(beer.getCalorie())
                .calorieUnit(beer.getCalorieUnit())
                .buyFrom(beer.getBuyFrom())
                .imageUrl(beer.getImageUrl())
                .likeCount(beer.getLikeCount())
                .reviewCount(beer.getReviewCount())
                .rate(beer.getRate())
                .categorySimpleResponse(CategorySimpleResponse.of(language, beer.getCategory()))
                .build();
    }
}
