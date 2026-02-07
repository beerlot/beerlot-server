package com.beerlot.pipeline.repository;

import com.beerlot.domain.brewery.Brewery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BreweryRepository extends JpaRepository<Brewery, Long> {
}
