package com.beerlot.pipeline;

import com.beerlot.domain.beer.Beer;
import com.beerlot.domain.category.CategoryInternational;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@EnableBatchProcessing
@SpringBootApplication
@EntityScan(basePackages = {"com.beerlot.domain"}, basePackageClasses = {Beer.class, CategoryInternational.class})
public class BeerLotPipelineApplication {

    /*
    CLI argument 에 filePath={CSV 파일 경로명} 지정 필요
     */

    public static void main(String[] args) {
        SpringApplication.run(BeerLotPipelineApplication.class, args);
    }

}
