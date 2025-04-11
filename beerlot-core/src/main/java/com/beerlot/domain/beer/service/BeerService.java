package com.beerlot.domain.beer.service;

import com.beerlot.domain.beer.Beer;
import com.beerlot.domain.beer.dto.response.BeerResponse;
import com.beerlot.domain.beer.dto.response.BeerSimpleResponse;
import com.beerlot.domain.beer.repository.BeerInternationalRepository;
import com.beerlot.domain.beer.repository.BeerRepository;
import com.beerlot.domain.common.entity.LanguageType;
import com.beerlot.domain.common.page.PageCustom;
import com.beerlot.domain.common.page.PageCustomRequest;
import com.beerlot.exception.ErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class BeerService {

    private final BeerRepository beerRepository;

    private final BeerInternationalRepository beerInternationalRepository;

    @Transactional(readOnly = true)
    public BeerResponse findBeerByIdAndLanguage(Long id, LanguageType language) {
        return BeerResponse.of(language, findBeerById(id));
    }

    @Transactional(readOnly = true)
    public List<BeerSimpleResponse> findTop10Beers(LanguageType language) {
        List<BeerSimpleResponse> beerResponseList = beerRepository.findTop10ByOrderByLikeCountDesc().stream()
                .map(beer -> BeerSimpleResponse.of(language, beer))
                .collect(Collectors.toList());
        return beerResponseList;
    }

    @Transactional(readOnly = true)
    public PageCustom<BeerSimpleResponse> findBeersBySearch(String keyword,
                                                            List<Long> categories,
                                                            List<String> countries,
                                                            Integer volumeMin,
                                                            Integer volumeMax,
                                                            LanguageType language, PageCustomRequest pageRequest) {
        return beerRepository.findBySearch(keyword, categories, countries, volumeMin, volumeMax, language, pageRequest);
    }

    @Transactional
    public Beer findBeerById(Long id) {
        return beerRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(ErrorMessage.BEER__NOT_EXIST.getMessage()));
    }

    @Transactional(readOnly = true)
    public PageCustom<BeerSimpleResponse> getBeerLikesByMember(String oauthId,
                                                               PageCustomRequest request,
                                                               LanguageType language) {
        return beerRepository.findByMember(oauthId, request, language);
    }

    @Transactional(readOnly = true)
    public List<String> getCountriesOfBeer(LanguageType languageType) {
        return beerInternationalRepository.getCountriesOfBeer(languageType);
    }
}
