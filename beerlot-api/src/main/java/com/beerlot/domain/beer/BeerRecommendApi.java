package com.beerlot.domain.beer;

import com.beerlot.annotation.CurrentUser;
import com.beerlot.domain.auth.security.oauth.entity.OAuthUserPrincipal;
import com.beerlot.domain.beer.dto.response.BeerRecommendResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@RequestMapping("/api/v1/beers")
public interface BeerRecommendApi {
    @Tag(name = "Beer Recommend API", description = "The Beer Recommend API.")
    @GetMapping("/recommend")
    ResponseEntity<BeerRecommendResponse> recommendBeer (
            @Parameter(hidden = true) @CurrentUser OAuthUserPrincipal userPrincipal
    );
}
