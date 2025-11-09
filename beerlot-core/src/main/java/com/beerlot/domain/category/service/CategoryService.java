package com.beerlot.domain.category.service;

import com.beerlot.domain.category.Category;
import com.beerlot.domain.category.CategoryInternational;
import com.beerlot.domain.category.CategoryInternationalId;
import com.beerlot.domain.category.dto.response.CategoryResponse;
import com.beerlot.domain.category.dto.response.CategorySearchResponse;
import com.beerlot.domain.category.repository.CategoryInternationalCustomRepository;
import com.beerlot.domain.category.repository.CategoryInternationalRepository;
import com.beerlot.domain.category.repository.CategoryRepository;
import com.beerlot.domain.common.entity.LanguageType;
import com.beerlot.exception.ErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryInternationalRepository categoryInternationalRepository;
    private final CategoryInternationalCustomRepository categoryInternationalCustomRepository;

    public Category findCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(ErrorMessage.CATEGORY__NOT_EXIST.getMessage()));
    }

    @Transactional(readOnly = true)
    public CategoryInternational findCategoryInternationalByKey(Long id, LanguageType language) {
        return categoryInternationalRepository.findById(new CategoryInternationalId(id, language))
                .orElseThrow(() -> new NoSuchElementException(ErrorMessage.CATEGORY_INTERNATIONAL__NOT_EXIST.getMessage()));
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategories(LanguageType languageType) {
        return categoryRepository.getTopLevelCategories()
                .stream()
                .map(o -> CategoryResponse.of(languageType, o))
                .filter(o -> !Objects.isNull(o))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CategorySearchResponse> getCategoryByName(String name, LanguageType language) {
        return categoryInternationalCustomRepository.findByNameAndLanguageType(name, language)
                .stream()
                .map(CategorySearchResponse::of)
                .toList();
    }
}
