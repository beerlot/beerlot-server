package com.beerlot.domain.review.dto.request;

import com.beerlot.domain.beer.Beer;
import com.beerlot.domain.member.Member;
import com.beerlot.domain.review.Review;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ReviewRequest {

    @JsonProperty(value = "content", required = true)
    private String content;

    @JsonProperty(value = "rate", required = true)
    private Float rate;

    @JsonProperty("image_url")
    private String imageUrl;

    @JsonProperty("buy_from")
    private String buyFrom;

    @Builder
    public ReviewRequest(String content, Float rate, String imageUrl, String buyFrom) {
        this.content = content;
        this.rate = rate;
        this.imageUrl = imageUrl;
        this.buyFrom = buyFrom;
    }

    public static Review to(ReviewRequest reviewRequest, Beer beer, Member member) {
        return Review.builder()
                .content(reviewRequest.getContent())
                .rate(reviewRequest.getRate())
                .imageUrl(reviewRequest.getImageUrl())
                .buyFrom(reviewRequest.getBuyFrom())
                .beer(beer)
                .member(member)
                .build();
    }
}
