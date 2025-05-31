package com.beerlot.domain.review.dto.response;

import com.beerlot.domain.member.dto.response.MemberResponse;
import com.beerlot.domain.review.Review;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@NoArgsConstructor
public class ReviewResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("content")
    private String content;

    @JsonProperty("image_url")
    private String imageUrl;

    @JsonProperty("rate")
    private Float rate;

    @JsonProperty("like_count")
    private Long likeCount;

    @JsonProperty("updated_at")
    private OffsetDateTime updatedAt;

    @JsonProperty("created_at")
    private OffsetDateTime createdAt;

    @JsonProperty("buy_from")
    private String buyFrom;

    @JsonProperty("member")
    private MemberResponse member;

    @JsonProperty("beer")
    private BeerResponse beer;


    @Builder
    public ReviewResponse(Long id, String content, String imageUrl, Float rate, Long likeCount,
                          OffsetDateTime createdAt, OffsetDateTime updatedAt, String buyFrom, MemberResponse member, BeerResponse beer) {
        this.id = id;
        this.content = content;
        this.imageUrl = imageUrl;
        this.rate = rate;
        this.likeCount = likeCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.buyFrom = buyFrom;
        this.member = member;
        this.beer = beer;
    }

    public static ReviewResponse of(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .content(review.getContent())
                .imageUrl(review.getImageUrl())
                .rate(review.getRate())
                .likeCount(review.getLikeCount())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .buyFrom(review.getBuyFrom())
                .member(MemberResponse.of(review.getMember()))
                .build();
    }

    @AllArgsConstructor
    @NoArgsConstructor
    public static class BeerResponse {
        @JsonProperty("id")
        private Long id;

        @JsonProperty("name")
        private String name;
    }
}
