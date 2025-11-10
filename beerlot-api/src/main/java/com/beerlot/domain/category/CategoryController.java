package com.beerlot.domain.category;

import com.beerlot.domain.category.dto.response.CategoryPediaResponse;
import com.beerlot.domain.category.dto.response.CategorySearchResponse;
import com.beerlot.domain.category.service.CategoryService;
import com.beerlot.domain.common.entity.LanguageType;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategorySearchResponse> searchCategory(
            @RequestParam("name") String name,
            @RequestParam("language") LanguageType languageType) {

        return categoryService.getCategoryByName(name, languageType);
    }

    @GetMapping(value = "/{id}")
    public List<CategoryPediaResponse> getCategoryDetail(
            @PathVariable("id") Long categoryId,
            @RequestParam("language") LanguageType languageType) {

        return categoryService.getCategoryDetail(categoryId, languageType);

    }
}
