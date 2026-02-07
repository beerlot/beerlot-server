package com.beerlot.domain.brewery;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BreweryInternational {

    @EmbeddedId
    private BreweryInternationalId breweryInternationalId;

    @MapsId("breweryId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brewery_id")
    private Brewery brewery;

    @Column
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Builder
    public BreweryInternational(BreweryInternationalId breweryInternationalId, Brewery brewery, String name, String description) {
        this.breweryInternationalId = breweryInternationalId;
        this.brewery = brewery;
        this.name = name;
        this.description = description;
    }
}

