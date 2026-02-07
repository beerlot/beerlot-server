package com.beerlot.domain.category.repository;

import com.beerlot.domain.category.CategoryInternational;
import com.beerlot.domain.common.entity.LanguageType;

import java.util.List;

public interface CategoryInternationalCustomRepository {
    List<CategoryInternational> findByNameAndLanguageType(String name, LanguageType languageType);
}
