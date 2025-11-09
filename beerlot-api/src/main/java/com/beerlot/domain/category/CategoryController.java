package com.beerlot.domain.category;

import com.beerlot.domain.category.dto.response.CategorySearchResponse;
import com.beerlot.domain.category.service.CategoryService;
import com.beerlot.domain.common.entity.LanguageType;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
