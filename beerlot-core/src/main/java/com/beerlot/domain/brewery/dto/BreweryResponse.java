package com.beerlot.domain.brewery.dto;

import com.beerlot.domain.beer.dto.response.BeerSimpleResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class BreweryResponse {

    String name;

    String imageUrl;

    String description;

    List<BeerSimpleResponse> beerList = new ArrayList<>();

    @Builder
    public BreweryResponse(String name, String imageUrl, String description) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.description = description;
    }

    public void appendBeer(BeerSimpleResponse beerSimpleResponse) {
        beerList.add(beerSimpleResponse);
    }
}
