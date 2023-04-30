package com.beerlot.pipeline.repository;

import com.beerlot.domain.category.CategoryInternational;
import com.beerlot.domain.category.CategoryInternationalId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryInternationalRepository extends JpaRepository<CategoryInternational, CategoryInternationalId> {

    CategoryInternational findCategoryInternationalByNameLike(String name);

}
