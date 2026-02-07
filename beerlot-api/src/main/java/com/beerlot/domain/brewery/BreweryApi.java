package com.beerlot.domain.brewery;

import com.beerlot.domain.brewery.dto.BreweryResponse;
import com.beerlot.domain.brewery.dto.BrewerySimpleResponse;
import com.beerlot.domain.common.entity.LanguageType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("/api/v1/breweries")
public interface BreweryApi {

    @Tag(name = "Breweries API", description = "The Brewery API.")
    @Operation(description = "Get All Breweries")
    @GetMapping
    ResponseEntity<List<BrewerySimpleResponse>> getBreweries(
            @RequestParam("languageType") LanguageType languageType);

    @Tag(name = "Breweries API", description = "The Brewery API.")
    @Operation(description = "Get one Brewery by ID And LanguageType")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "Success."),
                    @ApiResponse(responseCode = "401", description = "No Authorization was found."),
                    @ApiResponse(responseCode = "403", description = "Member does not have proper rights."),
                    @ApiResponse(responseCode = "404", description = "Brewery does not exist."),
                    @ApiResponse(responseCode = "409", description = "Member already liked the beer.")
            }
    )
    @GetMapping("/{id}")
    ResponseEntity<BreweryResponse> getBrewery(@PathVariable("id") Long breweryId,
                                                      @RequestParam("languageType") LanguageType languageType);



}
