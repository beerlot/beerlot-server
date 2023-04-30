package com.beerlot.pipeline.writer;

import com.beerlot.domain.beer.Beer;
import com.beerlot.domain.beer.BeerInternational;
import com.beerlot.domain.category.Category;
import com.beerlot.domain.category.CategoryInternational;
import com.beerlot.domain.common.entity.LanguageType;
import com.beerlot.pipeline.repository.BeerRepository;
import com.beerlot.pipeline.repository.CategoryInternationalRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
@Slf4j
@Component
@StepScope
public class BeerItemWriter implements ItemWriter<Map<String, String>> {

    @Autowired
    private BeerRepository beerRepository;

    @Autowired
    private CategoryInternationalRepository categoryInternationalRepository;

    @Override
    public void write(List<? extends Map<String, String>> items) throws Exception {
        for (Map<String, String> item : items) {
            CategoryInternational categoryInternational = categoryInternationalRepository.findCategoryInternationalByNameLike(item.get("Category"));
            if (categoryInternational == null) {
                log.info("해당 카테고리의 키 값을 찾을 수 없습니다 : {}", item.get("Category"));
                continue;
            }

            Category category = categoryInternational.getCategory();

            if (category == null) {
                System.out.println("Category 가 null 입니다.");
                continue;
            }

            Beer beer = Beer.builder()
                    .category(category)
                    .volume(Float.parseFloat(item.get("Volume")))
                    .imageUrl(item.get("ImageUrl"))
                    .calorie(Integer.parseInt(item.get("Calorie")))
                    .calorieUnit(Integer.parseInt(item.get("CalorieUnit")))
                    .brewery(item.get("Brewery"))
                    .build();

            for (LanguageType type : LanguageType.values()) {
                BeerInternational.builder()
                        .name(item.get("Name_" + type.name()))
                        .description(item.get("Description_" + type.name()))
                        .originCity(item.get("OriginCity_" + type.name()))
                        .originCountry(item.get("OriginCountry_" + type.name()))
                        .language(type)
                        .beer(beer)
                        .build();
            }

            beerRepository.save(beer);
        }
    }
}
