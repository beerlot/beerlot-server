package com.beerlot.domain.review.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;

import javax.persistence.Embedded;
import java.time.OffsetDateTime;

@NoArgsConstructor
public class ReviewArchiveResponse {

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

    @JsonProperty("created_at")
    private OffsetDateTime createdAt;

    @JsonProperty("updated_at")
    private OffsetDateTime updatedAt;

    @Embedded
    @JsonProperty("beer")
    private BeerResponse beer;


    public static class BeerResponse {
        @JsonProperty("id")
        private Long id;

        @JsonProperty("name")
        private String name;

        @JsonProperty("image_url")
        private String imageUrl;
    }
}