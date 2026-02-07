package com.beerlot.domain.glassware;

import com.beerlot.domain.common.entity.LanguageType;
import com.beerlot.domain.glassware.dto.response.GlasswareResponse;
import com.beerlot.domain.glassware.service.GlasswareService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/glassware")
@RequiredArgsConstructor
public class GlasswareController {

    private final GlasswareService glasswareService;

    @GetMapping
    public ResponseEntity<List<GlasswareResponse>> searchGlassware(
            @RequestParam("categoryId") Long categoryId,
            @RequestParam("language") LanguageType languageType) {
        LanguageType.validate(languageType);
        return new ResponseEntity<>(
                glasswareService.findGlasswareByCategoryId(categoryId, languageType),
                HttpStatus.OK
        );
    }
}
