package com.beerlot.domain.beer.controller;

import com.beerlot.domain.beer.BeerSortType;
import com.beerlot.domain.beer.dto.request.BeerSearchParam;
import com.beerlot.domain.beer.dto.response.BeerPage;
import com.beerlot.domain.beer.dto.response.BeerResponse;
import com.beerlot.domain.beer.service.BeerLikeService;
import com.beerlot.domain.beer.service.BeerService;
import com.beerlot.domain.common.entity.LanguageType;
import com.beerlot.domain.common.page.PageCustom;
import com.beerlot.domain.common.page.PageCustomRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BeerController implements BeerApi, BeerLikeApi {

    private final BeerService beerService;
    private final BeerLikeService beerLikeService;


    @Override
    public ResponseEntity<BeerResponse> findBeerById (Long beerId, LanguageType language) {
        LanguageType.validate(language);
        return new ResponseEntity<>(beerService.findBeerByIdAndLanguage(beerId, language), HttpStatus.OK);
    }

    /*TODO: Implement*/
    @Override
    public ResponseEntity<List<BeerResponse>> findTop10Beers (LanguageType language) {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<BeerPage> findBeersBySearch (BeerSearchParam beerSearchParam, Integer page, Integer size, BeerSortType sort, LanguageType language) {
        LanguageType.validate(language);
        PageCustom<BeerResponse> beerResponsePage = beerService.findBeersBySearch(beerSearchParam, language, new PageCustomRequest(page, size, sort));
        if (beerResponsePage.getContents().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(new BeerPage(beerResponsePage.getContents(), beerResponsePage.getPageRequest(), beerResponsePage.getTotalElements()), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> createBeerLike (Long beerId) {
        beerLikeService.likeBeer(beerId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> deleteBeerLike (Long beerId) {
        beerLikeService.unlikeBeer(beerId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
