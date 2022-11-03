package com.beerlot.core.domain.beer.service;

import com.beerlot.api.generated.model.BeerResponse;
import com.beerlot.core.domain.beer.Country;
import com.beerlot.core.domain.beer.repository.BeerRepository;
import com.beerlot.core.domain.beer.util.BeerResponseHelper;
import com.beerlot.core.domain.beer.util.sort.BeerSortType;
import com.beerlot.core.domain.category.repository.CategoryRepository;
import com.beerlot.core.domain.common.page.PageCustom;
import com.beerlot.core.domain.common.page.PageCustomRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BeerService {

    @Autowired
    private BeerRepository beerRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public BeerResponse findBeerById(Long id) {
        return BeerResponseHelper.of(beerRepository.findById(id).get());
    }

    @Transactional(readOnly = true)
    public PageCustom<BeerResponse> findBeersBySearch(String keyword, List<Long> categoryIds, List<String> countries, List<Integer> volumes, int page, int size, BeerSortType sort) {
        PageCustomRequest pageRequest = new PageCustomRequest(page, size, sort);
        List<Country> parsedCountries = countries == null ? null : Country.valuesOf(countries);
        return beerRepository.findBySearch(keyword, categoryIds, parsedCountries, volumes, pageRequest);
    }

    public List<BeerResponse> findTop10Beers() {
        List<BeerResponse> beerResponseList = beerRepository.findTop10ByOrderByLikeCountDesc().stream().map(BeerResponseHelper::of).collect(Collectors.toList());
        return beerResponseList;
    }
}
