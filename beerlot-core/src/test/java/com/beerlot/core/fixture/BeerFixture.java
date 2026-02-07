package com.beerlot.core.fixture;

import com.beerlot.domain.beer.Beer;
import com.beerlot.domain.category.Category;

public class BeerFixture {
    public static Beer createBeer(Long id, Category category) {
        return Beer.builder()
                .id(id)
                .category(category)
                .build();
    }
}
