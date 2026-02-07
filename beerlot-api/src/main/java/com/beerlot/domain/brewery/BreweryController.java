package com.beerlot.domain.brewery;

import com.beerlot.domain.brewery.dto.BreweryResponse;
import com.beerlot.domain.brewery.dto.BrewerySimpleResponse;
import com.beerlot.domain.brewery.service.BreweryService;
import com.beerlot.domain.common.entity.LanguageType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/breweries")
public class BreweryController {

    private final BreweryService breweryService;

    @GetMapping
    public ResponseEntity<List<BrewerySimpleResponse>> getBreweries(LanguageType languageType) {
        return new ResponseEntity<>(breweryService.getBreweries(languageType), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BreweryResponse> getBrewery(@PathVariable("id") Long breweryId,
                                      LanguageType languageType) {

        return new ResponseEntity<>(breweryService.getBrewery(breweryId, languageType), HttpStatus.OK);
    }
}
