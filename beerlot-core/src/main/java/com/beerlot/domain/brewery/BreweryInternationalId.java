package com.beerlot.domain.brewery;

import com.beerlot.domain.common.entity.LanguageType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
@Getter
public class BreweryInternationalId implements Serializable {

    private Long breweryId;

    @Enumerated(EnumType.STRING)
    private LanguageType languageType;

    public BreweryInternationalId(Long breweryId, LanguageType languageType) {
        this.breweryId = breweryId;
        this.languageType = languageType;
    }
}
