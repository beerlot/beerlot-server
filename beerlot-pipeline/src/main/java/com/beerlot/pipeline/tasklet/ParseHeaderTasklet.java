package com.beerlot.pipeline.tasklet;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Scanner;

@Component
@StepScope
public class ParseHeaderTasklet implements Tasklet {

    @Value("#{jobParameters[filePath]}")
    private String filePath;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        Scanner scanner = new Scanner(new File(filePath));
        String header = scanner.next();

        if (header == null) {
            throw new IllegalArgumentException("헤더가 존재하지 않습니다.");
        }

        contribution.getStepExecution().getJobExecution()
                .getExecutionContext().put("header", header);

        return RepeatStatus.FINISHED;
    }
}
