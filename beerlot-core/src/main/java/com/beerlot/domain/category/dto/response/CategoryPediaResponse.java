package com.beerlot.domain.category.dto.response;

import com.beerlot.domain.category.CategoryInternational;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CategoryPediaResponse {

    private Long id;

    private String name;

    private String description;

    private String imageUrl;

    @Builder
    public CategoryPediaResponse(Long id, String name, String description, String imageUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public static CategoryPediaResponse of(CategoryInternational categoryInternational) {
        return CategoryPediaResponse.builder()
                .id(categoryInternational.getId().getCategoryId())
                .name(categoryInternational.getName())
                .description(categoryInternational.getDescription())
                .imageUrl("")
                .build();
    }

}
