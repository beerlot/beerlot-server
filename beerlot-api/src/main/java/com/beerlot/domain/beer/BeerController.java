package com.beerlot.domain.beer;

import com.beerlot.domain.auth.security.oauth.entity.OAuthUserPrincipal;
import com.beerlot.domain.beer.dto.response.BeerPage;
import com.beerlot.domain.beer.dto.response.BeerRecommendResponse;
import com.beerlot.domain.beer.dto.response.BeerResponse;
import com.beerlot.domain.beer.dto.response.BeerSimpleResponse;
import com.beerlot.domain.beer.service.BeerLikeService;
import com.beerlot.domain.beer.service.BeerRecommendService;
import com.beerlot.domain.beer.service.BeerService;
import com.beerlot.domain.category.dto.response.CategoryResponse;
import com.beerlot.domain.category.service.CategoryService;
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
public class BeerController implements BeerApi, BeerLikeApi, BeerRecommendApi {

    private final BeerService beerService;
    private final BeerLikeService beerLikeService;
    private final BeerRecommendService beerRecommendService;
    private final CategoryService categoryService;

    @Override
    public ResponseEntity<BeerResponse> findBeerById (LanguageType language, Long beerId) {
        LanguageType.validate(language);
        return new ResponseEntity<>(beerService.findBeerByIdAndLanguage(beerId, language), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<BeerSimpleResponse>> findTop10Beers (LanguageType language) {
        LanguageType.validate(language);
        return new ResponseEntity<>(beerService.findTop10Beers(language), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<BeerPage> findBeersBySearch (String keyword,
                                                       List<Long> categories,
                                                       List<String> countries,
                                                       Integer volumeMin,
                                                       Integer volumeMax,
                                                       Integer page, Integer size, BeerSortType sort, LanguageType language) {
        LanguageType.validate(language);
        PageCustom<BeerSimpleResponse> beerResponsePage = beerService.findBeersBySearch(keyword, categories, countries, volumeMin, volumeMax, language, new PageCustomRequest(page, size, sort));
        return new ResponseEntity<>(new BeerPage(beerResponsePage.getContents(), beerResponsePage.getPageRequest(), beerResponsePage.getTotalElements()), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> createBeerLike (OAuthUserPrincipal userPrincipal, Long beerId) {
        beerLikeService.likeBeer(userPrincipal.getOauthId(), beerId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> deleteBeerLike (OAuthUserPrincipal userPrincipal, Long beerId) {
        beerLikeService.unlikeBeer(userPrincipal.getOauthId(), beerId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<BeerRecommendResponse> recommendBeer (OAuthUserPrincipal userPrincipal) {
        return new ResponseEntity<>(beerRecommendService.recommend(userPrincipal.getOauthId()), HttpStatus.OK);

    }

    @Override
    public ResponseEntity<List<CategoryResponse>> getCategories(LanguageType language) {
        return new ResponseEntity<>(categoryService.getCategories(language), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<String>> getCountries(LanguageType language) {
        return new ResponseEntity<>(beerService.getCountriesOfBeer(language), HttpStatus.OK);
    }
}
