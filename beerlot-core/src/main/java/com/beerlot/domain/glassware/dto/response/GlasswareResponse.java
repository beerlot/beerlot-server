package com.beerlot.domain.glassware.dto.response;

import com.beerlot.domain.glassware.Glassware;
import com.beerlot.domain.glassware.GlasswareInternational;
import com.beerlot.domain.common.entity.LanguageType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class GlasswareResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("image_url")
    private String imageUrl;

    @Builder
    public GlasswareResponse(Long id, String name, String description, String imageUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public static GlasswareResponse of(LanguageType language, Glassware glassware) {
        GlasswareInternational glasswareInternational = glassware.getGlasswareInternationals().stream()
                .filter(gi -> gi.getId().getLanguage().equals(language))
                .findFirst()
                .orElse(null);

        if (glasswareInternational == null) {
            return null;
        }

        return GlasswareResponse.builder()
                .id(glassware.getId())
                .name(glasswareInternational.getName())
                .description(glasswareInternational.getDescription())
                .imageUrl(glassware.getImageUrl())
                .build();
    }
}
