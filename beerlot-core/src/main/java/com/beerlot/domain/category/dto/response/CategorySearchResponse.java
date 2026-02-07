package com.beerlot.domain.category.dto.response;

import com.beerlot.domain.category.CategoryInternational;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CategorySearchResponse {

    private Long id;

    private String name;

    private String imageUrl;

    @Builder
    public CategorySearchResponse(Long id, String name, String imageUrl) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public static CategorySearchResponse of(CategoryInternational categoryInternational) {
        return new CategorySearchResponse(
                categoryInternational.getId().getCategoryId(),
                categoryInternational.getName(),
                ""
        );
    }
}
