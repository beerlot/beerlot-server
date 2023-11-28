package com.beerlot.pipeline.repository;

import com.beerlot.domain.category.CategoryInternational;
import com.beerlot.domain.category.CategoryInternationalId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CategoryInternationalRepository extends JpaRepository<CategoryInternational, CategoryInternationalId> {

    @Query("select m from CategoryInternational m where m.name like concat('%', :name, '%')")
    CategoryInternational findCategoryInternationalByNameLike(String name);

}
