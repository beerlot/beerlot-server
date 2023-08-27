package com.beerlot.domain.brewery;

import com.beerlot.domain.beer.Beer;
import com.beerlot.domain.common.entity.LanguageType;
import lombok.Getter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Getter
public class Brewery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "brewery_id")
    private Long breweryId;

    @OneToMany(mappedBy = "brewery")
    private List<BreweryInternational> breweryInternationals = new ArrayList<>();

    @OneToMany(mappedBy = "brewery")
    private List<Beer> beers = new ArrayList<>();

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    public String getName(LanguageType languageType) {
        Optional<BreweryInternational> maybeBreweryInternational = breweryInternationals.stream()
                .filter(o -> o.getBreweryInternationalId().getLanguageType().equals(languageType))
                .findFirst();

        return maybeBreweryInternational.isEmpty() ? "" : maybeBreweryInternational.get().getName();
    }

}