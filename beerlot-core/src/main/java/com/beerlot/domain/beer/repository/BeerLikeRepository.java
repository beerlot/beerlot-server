package com.beerlot.domain.beer.repository;

import com.beerlot.domain.beer.BeerLike;
import com.beerlot.domain.beer.BeerLikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BeerLikeRepository extends JpaRepository<BeerLike, BeerLikeId> {
    boolean existsByBeer_IdAndMember_Id(Long beerId, Long memberId);
    void deleteByBeer_IdAndMember_Id(Long beerId, Long memberId);
    List<BeerLike> findByMember_Id(Long memberId);
}
