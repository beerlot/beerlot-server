package com.beerlot.pipeline.tasklet;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;

@Component
@StepScope
public class ParseHeaderTasklet implements Tasklet {

    @Value("#{jobParameters[filePath]}")
    private String filePath;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String header = br.readLine();

        if (header == null) {
            throw new IllegalArgumentException("헤더가 존재하지 않습니다.");
        }

        contribution.getStepExecution().getJobExecution()
                .getExecutionContext().put("header", header);

        return RepeatStatus.FINISHED;
    }
}
