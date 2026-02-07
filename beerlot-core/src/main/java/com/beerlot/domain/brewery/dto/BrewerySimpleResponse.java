package com.beerlot.domain.brewery.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class BrewerySimpleResponse {

    String name;

    String imageUrl;

    String description;

    @Builder
    public BrewerySimpleResponse(String name, String imageUrl, String description) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.description = description;
    }
}
