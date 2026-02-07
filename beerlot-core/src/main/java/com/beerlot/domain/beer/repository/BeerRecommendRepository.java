package com.beerlot.domain.beer.repository;

import java.util.List;
import java.util.Map;

public interface BeerRecommendRepository {

    List<Long> getRecommendBeer(Map<Long, Long> elements, String oauthId);
}
