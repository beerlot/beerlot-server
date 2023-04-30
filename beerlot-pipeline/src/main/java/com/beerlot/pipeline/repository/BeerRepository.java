package com.beerlot.pipeline.repository;

import com.beerlot.domain.beer.Beer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BeerRepository extends JpaRepository<Beer, Long> {
}
