package com.beerlot.domain.category.controller;

import com.beerlot.domain.category.dto.response.CategoryPediaResponse;
import com.beerlot.domain.category.dto.response.CategorySearchResponse;
import com.beerlot.domain.category.service.CategoryService;
import com.beerlot.domain.common.entity.LanguageType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// FIXME 추후 OAuthFilter Bean Exclude 후 WebMvcTest로 이전
@SpringBootTest
@AutoConfigureMockMvc
class CategoryControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    @Test
    void searchCategoryByName() throws Exception {
        List<CategorySearchResponse> stub = List.of(
                new CategorySearchResponse(1L, "Ale", ""),
                new CategorySearchResponse(2L, "Pale Ale", "")
        );

        Mockito.when(categoryService.getCategoryByName(eq("Ale"), eq(LanguageType.EN)))
                .thenReturn(stub);

        mockMvc.perform(get("/api/v1/category")
                        .param("name", "Ale")
                        .param("language", "EN"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Ale"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Pale Ale"));

        Mockito.verify(categoryService, Mockito.times(1))
                .getCategoryByName(eq("Ale"), eq(LanguageType.EN));
    }

    @Test
    void getCategoryDetailById() throws Exception {
        CategoryPediaResponse responseOfAle = new CategoryPediaResponse(1L, "Ale", "Description Of Ale", "");
        List<CategoryPediaResponse> stub = List.of(responseOfAle);

        when(categoryService.getCategoryDetail(eq(1L), eq(LanguageType.EN))).thenReturn(stub);

        mockMvc.perform(get("/api/v1/category/" + responseOfAle.getId())
                .param("language", "EN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(responseOfAle.getId()))
                .andExpect(jsonPath("$[0].name").value(responseOfAle.getName()))
                .andExpect(jsonPath("$[0].description").value(responseOfAle.getDescription()))
                .andExpect(jsonPath("$[0].imageUrl").value(responseOfAle.getImageUrl()));


        Mockito.verify(categoryService,  Mockito.times(1))
                .getCategoryDetail(eq(1L), eq(LanguageType.EN));
    }
}