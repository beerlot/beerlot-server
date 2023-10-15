package com.beerlot.pipeline.repository;

import com.beerlot.domain.brewery.BreweryInternational;
import com.beerlot.domain.brewery.BreweryInternationalId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BreweryInternationalRepository extends JpaRepository<BreweryInternational, BreweryInternationalId> {

    List<BreweryInternational> findBreweriesInternationalsByNameLike(String name);

}
