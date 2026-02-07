package com.beerlot.core.fixture;

import com.beerlot.domain.category.Category;

public class CategoryFixture {
    public static Category createCategory(Long id, Category parent) {
        return Category.builder()
                .id(id)
                .parent(parent)
                .build();
    }
}
