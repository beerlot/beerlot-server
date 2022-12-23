package com.beerlot.core.domain.review.dto.response;

import com.beerlot.core.domain.common.dto.BaseResponse;
import com.beerlot.core.domain.review.Review;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import org.joda.time.DateTime;

public class ReviewResponse extends BaseResponse {

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

    @JsonProperty("beer_id")
    private Long beerId;

    @Builder
    public ReviewResponse(Long id, String content, String imageUrl, Float rate, Long likeCount, Long beerId,
                          DateTime createdAt, DateTime updatedAt) {
        this.id = id;
        this.content = content;
        this.imageUrl = imageUrl;
        this.rate = rate;
        this.likeCount = likeCount;
        this.beerId = beerId;
        setCreatedAt(createdAt);
        setUpdatedAt(updatedAt);
    }

    public static ReviewResponse of(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .content(review.getContent())
                .imageUrl(review.getImageUrl())
                .rate(review.getRate())
                .likeCount(review.getLikeCount())
                .beerId(review.getBeer().getId())
                .createdAt(getCreatedAt())
                .updatedAt(getUpdatedAt())
                .build();
    }
}
