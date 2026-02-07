package com.beerlot.core.domain.beer.service;

import com.beerlot.domain.beer.Beer;
import com.beerlot.domain.beer.service.BeerLikeService;
import com.beerlot.domain.beer.service.BeerRecommendService;
import com.beerlot.domain.beer.service.BeerRecommendService.MappedCategory;
import com.beerlot.domain.category.Category;
import com.beerlot.domain.member.service.MemberService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.beerlot.core.fixture.BeerFixture.createBeer;
import static com.beerlot.core.fixture.CategoryFixture.createCategory;

@ExtendWith(MockitoExtension.class)
//@MockitoSettings(strictness = Strictness.LENIENT)
public class BeerRecommendServiceTest {
    @InjectMocks
    private BeerRecommendService beerRecommendService;
    @Mock
    private MemberService memberService;
    @Mock
    private BeerLikeService beerLikeService;

    Category parentCategory1 = createCategory(1L, null);
    Category parentCategory2 = createCategory(2L, null);
    Category childCategory1_1 = createCategory(11L, parentCategory1);
    Category childCategory1_2 = createCategory(12L, parentCategory1);
    Category childCategory2_1 = createCategory(21L, parentCategory2);
    Beer beer1 = createBeer(1L, childCategory1_1);
    Beer beer2 = createBeer(2L, childCategory1_2);
    Beer beer3 = createBeer(3L, childCategory2_1);

    @Nested
    class Aggregate {
        @Test
        public void should_aggregate_when_sameParentCategory() {
            List<Beer> likedBeers = Arrays.asList(beer1, beer2);

            MappedCategory aggregated = beerRecommendService.aggregate(likedBeers);

            Assertions.assertEquals(1, aggregated.getParents().size());
            Assertions.assertEquals(2, aggregated.getChildren().size());
        }

        @Test
        public void should_aggregate_when_differentParentCategory() {
            List<Beer> likedBeers = Arrays.asList(beer1, beer3);

            MappedCategory aggregated = beerRecommendService.aggregate(likedBeers);

            Assertions.assertEquals(2, aggregated.getParents().size());
            Assertions.assertEquals(2, aggregated.getChildren().size());
        }

        @Test
        public void should_aggregate_when_sameChildrenCategory() {
            List<Beer> likedBeers = Arrays.asList(beer1, beer1);

            MappedCategory aggregated = beerRecommendService.aggregate(likedBeers);

            Assertions.assertEquals(1, aggregated.getParents().size());
            Assertions.assertEquals(1, aggregated.getChildren().size());
        }
    }

    @Nested
    class Exclude {
        @Test
        public void should_excludeAll_when_weightIsOne() {
            Map<Category, Long> parents = Map.of(parentCategory1, 1L);
            Map<Category, Long> children = Map.of(childCategory1_1, 1L);

            MappedCategory excluded = beerRecommendService.exclude(new MappedCategory(parents, children));

            Assertions.assertEquals(0, excluded.getParents().size());
            Assertions.assertEquals(0, excluded.getChildren().size());
        }

        @Test
        public void should_excludeOneFromParentCategory_when_allChildrenAreSame() {
            Map<Category, Long> parents = Map.of(parentCategory1, 2L);
            Map<Category, Long> children = Map.of(childCategory1_1, 2L);

            MappedCategory excluded = beerRecommendService.exclude(new MappedCategory(parents, children));

            Assertions.assertEquals(0, excluded.getParents().size());
            Assertions.assertEquals(1, excluded.getChildren().size());
            Assertions.assertEquals(2, excluded.getChildren().get(childCategory1_1));
        }

        @Test
        public void should_excludeNothingFromParentCategory_when_allChildrenAreSame() {
            Map<Category, Long> parents = Map.of(parentCategory1, 3L);
            Map<Category, Long> children = Map.of(childCategory1_1, 2L, childCategory1_2, 1L);

            MappedCategory excluded = beerRecommendService.exclude(new MappedCategory(parents, children));

            Assertions.assertEquals(1, excluded.getParents().size());
            Assertions.assertEquals(3, excluded.getParents().get(parentCategory1));
            Assertions.assertEquals(1, excluded.getChildren().size());
            Assertions.assertEquals(2, excluded.getChildren().get(childCategory1_1));
        }
    }

    @Nested
    class GetGcd {
        @Test
        public void should_getGcdFromList_when_twoWeightsAreGiven() {
            Map<Category, Long> parents = Map.of(parentCategory1, 2L);
            Map<Category, Long> children = Map.of(childCategory1_1, 2L);

            long gcd = beerRecommendService.getGcd(new MappedCategory(parents, children));

            Assertions.assertEquals(2L, gcd);
        }

        @Test
        public void should_getGcdFromList_when_threeWeightsAreGiven() {
            Map<Category, Long> parents = Map.of(parentCategory1, 4L);
            Map<Category, Long> children = Map.of(childCategory1_1, 2L, childCategory1_2, 2L);

            long gcd = beerRecommendService.getGcd(new MappedCategory(parents, children));

            Assertions.assertEquals(2L, gcd);
        }

        @Test
        public void should_getGcdFromList_when_parentsIsEmpty() {
            Map<Category, Long> parents = new HashMap<>();
            Map<Category, Long> children = Map.of(childCategory1_1, 2L);

            long gcd = beerRecommendService.getGcd(new MappedCategory(parents, children));

            Assertions.assertEquals(2L, gcd);
        }
    }

    /*@Nested
    class Recommend {
        // TODO: Make this test work!!!!!
        @Test
        public void should_recommend() {
            Mockito.when(memberService.findMemberByOauthId(isA(String.class)))
                    .thenReturn(new Member());
            Mockito.lenient().when(beerLikeService.getLikedBeers(isA(Long.class)))
                    .thenReturn(Arrays.asList(beer1, beer3)); // error: The test uses real service instead of mocked service.

            Map<Long, Long> recommended = beerRecommendService.recommend("1234567890");

            Assertions.assertEquals(1, recommended.size());
            //Assertions.assertEquals(parentCategory1.getId(), recommended.get(0));
        }
    }*/
}
