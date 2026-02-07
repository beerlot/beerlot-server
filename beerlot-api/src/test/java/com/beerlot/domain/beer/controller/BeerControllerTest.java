package com.beerlot.domain.beer.controller;

import com.beerlot.domain.auth.security.oauth.entity.OAuthUserPrincipal;
import com.beerlot.domain.beer.Beer;
import com.beerlot.domain.beer.service.BeerLikeService;
import com.beerlot.domain.beer.service.BeerService;
import com.beerlot.domain.category.service.CategoryService;
import com.beerlot.domain.common.entity.LanguageType;
import com.beerlot.domain.member.Member;
import com.beerlot.exception.ConflictException;
import com.beerlot.tool.fixture.BeerFixture;
import com.beerlot.tool.fixture.MemberFixture;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class BeerControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BeerService beerService;

    @MockBean
    BeerLikeService beerLikeService;

    @MockBean
    CategoryService categoryService;

    Beer beer;

    @Nested
    class BeerTest {
        @Nested
        class FindByIdTest {

            @BeforeEach
            public void setUp() {
                beer = BeerFixture.createBeer();
            }

            @Test
            public void success() throws Exception {
                Mockito.when(beerService.findBeerById(isA(Long.class)))
                                .thenReturn(beer);

                mockMvc.perform(get("/api/v1/beers/{beerId}", 1)
                                .param("language", String.valueOf(LanguageType.KR))
                        )
                        .andExpect(status().isOk());
            }

            @Test
            public void beerNotExist() throws Exception {
                Mockito.when(beerService.findBeerByIdAndLanguage(2L, LanguageType.KR))
                        .thenThrow(NoSuchElementException.class);

                mockMvc.perform(get("/api/v1/beers/{beerId}", 2)
                                .param("language", String.valueOf(LanguageType.KR))
                        )
                        .andExpect(status().isNotFound());
            }
        }

        @Nested
        class FindBySearchTest {
        }

        @Nested
        class FindTop10Test {
            @Test
            public void success() throws Exception {
                Mockito.when(beerService.findTop10Beers(isA(LanguageType.class)))
                        .thenReturn(List.of());

                mockMvc.perform(get("/api/v1/beers/top")
                                .param("language", String.valueOf(LanguageType.KR))
                        )
                        .andExpect(status().isOk());
            }
        }
    }


    @Nested
    class BeerLikeTest {

        Member member;

        @BeforeEach
        public void setUp() {
            member = MemberFixture.createMember();
        }

        @Nested
        class CreateBeerLikeTest {

            @Test
            public void success() throws Exception {
                doNothing().when(beerLikeService).likeBeer(isA(String.class), isA(Long.class));

                mockMvc.perform(post("/api/v1/beers/{beerId}/likes", 1)
                                .with(user(OAuthUserPrincipal.of(member)))
                        )
                        .andExpect(status().isCreated());
            }

            @Test
            public void beerNotExist() throws Exception {
                doThrow(NoSuchElementException.class).when(beerLikeService).likeBeer(isA(String.class), isA(Long.class));

                mockMvc.perform(post("/api/v1/beers/{beerId}/likes", 2)
                                .with(user(OAuthUserPrincipal.of(member)))
                        )
                        .andExpect(status().isNotFound());
            }

            @Test
            public void beerLikeAlreadyExist() throws Exception {
                doThrow(ConflictException.class).when(beerLikeService).likeBeer(isA(String.class), isA(Long.class));

                mockMvc.perform(post("/api/v1/beers/{beerId}/likes", 1)
                                .with(user(OAuthUserPrincipal.of(member)))
                        )
                        .andExpect(status().isConflict());
            }
        }

        @Nested
        class DeleteBeerLikeTest {

            @Test
            public void success() throws Exception {
                doNothing().when(beerLikeService).unlikeBeer(isA(String.class), isA(Long.class));

                mockMvc.perform(delete("/api/v1/beers/{beerId}/likes", 1)
                                .with(user(OAuthUserPrincipal.of(member)))
                        )
                        .andExpect(status().isNoContent());
            }

            @Test
            public void beerOrBeerLikeNotExist() throws Exception {
                doThrow(NoSuchElementException.class).when(beerLikeService).unlikeBeer(isA(String.class), isA(Long.class));

                mockMvc.perform(delete("/api/v1/beers/{beerId}/likes", 2)
                                .with(user(OAuthUserPrincipal.of(member)))
                        )
                        .andExpect(status().isNotFound());
            }
        }

        @Nested
        class BeerCategoryTest {
            @Test
            public void success() throws Exception {
                Mockito.when(categoryService.getCategories(LanguageType.KR))
                        .thenReturn(new ArrayList<>());

                mockMvc.perform(get("/api/v1/beers/category")
                                .param("language", String.valueOf(LanguageType.KR))
                        )
                        .andExpect(status().isOk());
            }
        }
    }

    @Nested
    class BeerCategoryTest {

        @Nested
        class GetCategoryTest {
            @Test
            public void success() throws Exception {
                Mockito.when(categoryService.getCategories(LanguageType.KR))
                        .thenReturn(new ArrayList<>());

                mockMvc.perform(get("/api/v1/beers/category")
                                .param("language", String.valueOf(LanguageType.KR))
                        )
                        .andExpect(status().isOk());
            }
        }
    }

    @Nested
    class BeerCountriesTest {

        @Nested
        class GetCountries {

            @Test
            public void success() throws Exception {
                List<String> countries = List.of("Category1", "Category2");
                String jsonValue = new ObjectMapper().writeValueAsString(countries);

                Mockito.when(beerService.getCountriesOfBeer(Mockito.any()))
                        .thenReturn(countries);

                mockMvc.perform(get("/api/v1/beers/countries")
                        .param("language", String.valueOf(LanguageType.KR))
                ).andExpect(status().isOk()).andExpect(content().string(jsonValue));
            }
        }
    }
}
