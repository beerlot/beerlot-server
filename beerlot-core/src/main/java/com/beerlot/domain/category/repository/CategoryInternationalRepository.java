package com.beerlot.domain.category.repository;

import com.beerlot.domain.category.CategoryInternational;
import com.beerlot.domain.category.CategoryInternationalId;
import com.beerlot.domain.common.entity.LanguageType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryInternationalRepository extends JpaRepository<CategoryInternational, CategoryInternationalId> {

    @Query("select m from CategoryInternational m join fetch Category c where m.id.categoryId = :categoryId and m.id.language = :language")
    List<CategoryInternational> findByCategoryIdAndLanguage(Long categoryId, LanguageType language);

}
