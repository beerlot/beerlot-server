package com.beerlot.domain.brewery.repository;

import com.beerlot.domain.brewery.BreweryInternational;
import com.beerlot.domain.brewery.BreweryInternationalId;
import com.beerlot.domain.common.entity.LanguageType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BreweryInternationalRepository extends JpaRepository<BreweryInternational, BreweryInternationalId> {

    @Query("select m from BreweryInternational m where m.breweryInternationalId.languageType = :languageType")
    List<BreweryInternational> findAllByLanguageType(@Param("languageType") LanguageType languageType);

    @Query("select m from BreweryInternational m " +
            "where m.breweryInternationalId.breweryId = :breweryId and m.breweryInternationalId.languageType = :languageType")
    Optional<BreweryInternational> findOneByBreweryIdAndLanguageType(@Param("breweryId") Long breweryId,
                                                                     @Param("languageType") LanguageType languageType);

}
