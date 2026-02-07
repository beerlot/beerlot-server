package com.beerlot.domain.beer.repository;

import com.beerlot.domain.beer.BeerInternational;
import com.beerlot.domain.beer.BeerInternationalId;
import com.beerlot.domain.common.entity.LanguageType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BeerInternationalRepository extends JpaRepository<BeerInternational, BeerInternationalId> {

    @Query("select distinct m.originCountry from BeerInternational m where m.id.language = :languageType")
    List<String> getCountriesOfBeer(@Param("languageType") LanguageType languageType);

}
