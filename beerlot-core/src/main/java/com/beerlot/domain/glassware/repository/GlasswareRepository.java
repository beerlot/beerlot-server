package com.beerlot.domain.glassware.repository;

import com.beerlot.domain.glassware.Glassware;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GlasswareRepository extends JpaRepository<Glassware, Long> {
    
    @Query("SELECT DISTINCT g FROM Glassware g " +
           "JOIN FETCH g.glasswareInternationals " +
           "JOIN g.categories c " +
           "WHERE c.id = :categoryId")
    List<Glassware> findByCategoryId(@Param("categoryId") Long categoryId);
}
