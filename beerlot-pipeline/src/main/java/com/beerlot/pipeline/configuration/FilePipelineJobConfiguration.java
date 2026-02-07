package com.beerlot.pipeline.configuration;


import com.beerlot.pipeline.tasklet.ParseHeaderTasklet;
import com.beerlot.pipeline.writer.BeerItemWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class FilePipelineJobConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final ParseHeaderTasklet parseHeaderTasklet;
    private final BeerItemWriter beerItemWriter;

    @Bean
    public Job filePipelineJob() {
        return jobBuilderFactory.get("filePipelineJob")
                .incrementer(new RunIdIncrementer())
                .start(parseHeaderStep())
                .next(beerFileStep())
                .build();
    }

    @Bean
    public Step parseHeaderStep() {
        return stepBuilderFactory.get("parseHeaderStep")
                .tasklet(parseHeaderTasklet)
                .build();
    }

    @Bean
    public Step beerFileStep() {
        return stepBuilderFactory.get("beerFileStep")
                .<Map<String, String>, Map<String, String>>chunk(10)
                .reader(flatFileItemReader(null, null))
                .processor(new ItemProcessor<Map<String, String>, Map<String, String>>() {
                    @Override
                    public Map<String, String> process(Map<String, String> item) throws Exception {
                        return item;
                    }
                })
                .writer(beerItemWriter)
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<Map<String, String>> flatFileItemReader(
            @Value("#{jobExecutionContext['header']}") String header,
            @Value("#{jobParameters['filePath']}") String filePath) {
        FlatFileItemReader<Map<String, String>> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(new FileSystemResource(new File(filePath)));

        DefaultLineMapper<Map<String, String>> defaultLineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer("\t");
        defaultLineMapper.setLineTokenizer(delimitedLineTokenizer);

        String[] headers = header.split("\t");
        delimitedLineTokenizer.setNames(headers);

        // 첫줄 헤더는 미리 읽었으므로 넘어간다.
        itemReader.setLinesToSkip(1);

        itemReader.setLineMapper(defaultLineMapper);
        defaultLineMapper.setFieldSetMapper(fieldSet -> {
            Map<String, String> result = new HashMap<>();
            for (int i = 0; i < headers.length; i++) {
                result.put(headers[i], fieldSet.readString(i));
            }

            return result;
        });

        return itemReader;
    }
}
