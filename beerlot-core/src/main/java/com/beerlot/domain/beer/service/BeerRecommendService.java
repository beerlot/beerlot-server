package com.beerlot.domain.beer.service;

import com.beerlot.domain.beer.Beer;
import com.beerlot.domain.beer.dto.response.BeerRecommendResponse;
import com.beerlot.domain.beer.repository.BeerRecommendRepository;
import com.beerlot.domain.category.Category;
import com.beerlot.domain.member.Member;
import com.beerlot.domain.member.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class BeerRecommendService {

    private final MemberService memberService;
    private final BeerLikeService beerLikeService;
    private final BeerRecommendRepository beerRecommendRepository;

    public BeerRecommendResponse recommend(String oauthId) {
        Member member = memberService.findMemberByOauthId(oauthId);
        List<Beer> likedBeers = beerLikeService.getLikedBeers(member.getId());

        MappedCategory aggregated = aggregate(likedBeers);
        MappedCategory excluded = exclude(aggregated);

        if (excluded.parents.size() == 0 && excluded.children.size() == 0) {
            return new BeerRecommendResponse(beerRecommendRepository.getRecommendBeer(
                    calculateRatio(new MappedCategory(null, aggregated.children), 1L),
                    oauthId));
        }
        else {
            long gcd = getGcd(excluded);
            return new BeerRecommendResponse(beerRecommendRepository.getRecommendBeer(
                    calculateRatio(excluded, gcd),
                    oauthId));
        }
    }

    public MappedCategory aggregate(List<Beer> beers) {
        Map<Category, Long> parents = new HashMap<>();
        Map<Category, Long> children = new HashMap<>();
        beers.stream().forEach(beer -> {
            if (beer.getCategory().getParent() != null) {
                parents.merge(beer.getCategory().getParent(), 1L, Long::sum);
            }

            children.merge(beer.getCategory(), 1L, Long::sum);
        });

        return new MappedCategory(parents, children);
    }

    public MappedCategory exclude(MappedCategory mappedCategory) {
        HashMap<Category, Long> parents = new HashMap<>(mappedCategory.parents);
        HashMap<Category, Long> children = new HashMap<>(mappedCategory.children);

        children.entrySet().forEach(child -> {
            if (Objects.equals(parents.get(child.getKey().getParent()), child.getValue())) {
                parents.remove(child.getKey().getParent());
            }
        });

        return new MappedCategory(parents, children);
    }

    public long getGcd(MappedCategory category) {
        List<Long> weights = new ArrayList<>();
        weights.addAll(category.parents.values());
        weights.addAll(category.children.values());

        if (weights.size() == 1) {
            return weights.get(0);
        }

        long gcd = gcd(weights.get(0), weights.get(1));
        for(int i= 2 ; i < weights.size() ; i++) {
            gcd = gcd(gcd, weights.get(i));
        }
        return gcd;
    }

    public Map<Long, Long> calculateRatio(MappedCategory category, long gcd) {
        Map<Long, Long> result = new HashMap<>();
        if (category.parents != null) {
            category.parents.entrySet().forEach(parent -> {
                result.put(parent.getKey().getId(), parent.getValue() / gcd);
            });
        }
        category.children.entrySet().forEach(child -> {
            result.put(child.getKey().getId(), child.getValue() / gcd);
        });

        return result;
    }

    private long gcd(long x, long y) {
        if (x%y == 0) {
            return y;
        }
        return gcd(y, x%y);
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class MappedCategory {
        Map<Category, Long> parents;
        Map<Category, Long> children;
    }
}
