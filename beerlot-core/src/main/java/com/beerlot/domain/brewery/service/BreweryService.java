package com.beerlot.domain.brewery.service;

import com.beerlot.domain.beer.Beer;
import com.beerlot.domain.beer.dto.response.BeerSimpleResponse;
import com.beerlot.domain.brewery.BreweryInternational;
import com.beerlot.domain.brewery.dto.BreweryResponse;
import com.beerlot.domain.brewery.dto.BrewerySimpleResponse;
import com.beerlot.domain.brewery.repository.BreweryInternationalRepository;
import com.beerlot.domain.brewery.repository.BreweryRepository;
import com.beerlot.domain.common.entity.LanguageType;
import com.beerlot.exception.ErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BreweryService {

    private final BreweryRepository breweryRepository;

    private final BreweryInternationalRepository breweryInternationalRepository;

    public List<BrewerySimpleResponse> getBreweries(LanguageType languageType) {
        return breweryInternationalRepository.findAllByLanguageType(languageType)
                .stream()
                .map(element -> new BrewerySimpleResponse(
                        element.getName(),
                        element.getBrewery().getImageUrl(),
                        element.getDescription()))
                .collect(Collectors.toList());
    }

    public BreweryResponse getBrewery(Long breweryId, LanguageType languageType) {
        BreweryInternational breweryInternational = breweryInternationalRepository.
                findOneByBreweryIdAndLanguageType(breweryId, languageType)
                .orElseThrow(() -> new NoSuchElementException(ErrorMessage.BREWERY_INTERNATIONAL__NOT_EXIST.getMessage()));

        BreweryResponse breweryResponse = new BreweryResponse(
                breweryInternational.getName(),
                breweryInternational.getBrewery().getImageUrl(),
                breweryInternational.getDescription());

        List<Beer> breweryBeers = breweryInternational.getBrewery().getBeers();
        for (Beer beer : breweryBeers) {
            breweryResponse.appendBeer(BeerSimpleResponse.of(languageType, beer));
        }

        return breweryResponse;
    }



}
