package com.beerlot.domain.beer;

import com.beerlot.domain.beer.dto.response.BeerPage;
import com.beerlot.domain.beer.dto.response.BeerResponse;
import com.beerlot.domain.beer.dto.response.BeerSimpleResponse;
import com.beerlot.domain.category.dto.response.CategoryResponse;
import com.beerlot.domain.common.entity.LanguageType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("/api/v1/beers")
public interface BeerApi {

    @Tag(name = "Beer API", description = "The Beer API.")
    @Operation(description = "Get one beer by ID")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Success."),
                    @ApiResponse(responseCode = "404", description = "Beer does not exist.")
            }
    )
    @GetMapping("/{beerId}")
    ResponseEntity<BeerResponse> findBeerById (
            @Parameter(description = "Language code") @RequestParam("language") LanguageType language,
            @Parameter(description = "Beer ID") @PathVariable("beerId") Long beerId
    );

    @Tag(name = "Beer API", description = "The Beer API.")
    @Operation(description = "Get top 10 beers")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Success.")
            }
    )
    @GetMapping("/top")
    ResponseEntity<List<BeerSimpleResponse>> findTop10Beers (
            @Parameter(description = "Language code") @RequestParam("language") LanguageType language
    );


    @Tag(name = "Beer Category API")
    @Operation(description = "Get category of beers")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Success.")
            }
    )
    @GetMapping("/category")
    ResponseEntity<List<CategoryResponse>> getCategories(
            @Parameter(description = "Language Code") @RequestParam("language") LanguageType language
    );

    @Tag(name = "Beer API", description = "The Beer API.")
    @Operation(description = "Get beers filtered by search parameters")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Success."),
                    @ApiResponse(responseCode = "204", description = "No search result.")
            }
    )
    @GetMapping
    ResponseEntity<BeerPage> findBeersBySearch (
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "categories", required = false) List<Long> categories,
            @RequestParam(value = "countries", required = false) List<String> countries,
            @RequestParam(value = "volume_min", required = false) Integer volumeMin,
            @RequestParam(value = "volume_max", required = false) Integer volumeMax,
            @RequestParam("page") Integer page,
            @RequestParam("size") Integer size,
            @RequestParam("sort") BeerSortType sort,
            @Parameter(description = "Language code") @RequestParam("language") LanguageType language
    );

    @Tag(name = "Beer API", description = "Get Countries Of Beer")
    @Operation(description = "Get Countries Of Beer")
    @GetMapping("/countries")
    ResponseEntity<List<String>> getCountries(
            @RequestParam(value = "language") LanguageType language
    );
}
