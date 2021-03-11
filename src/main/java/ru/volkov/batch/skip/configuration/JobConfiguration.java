package ru.volkov.batch.skip.configuration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import ru.volkov.batch.skip.components.CustomRetryableException;
import ru.volkov.batch.skip.components.SkipItemProcessor;
import ru.volkov.batch.skip.components.SkipItemWriter;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class JobConfiguration {

    private StepBuilderFactory stepBuilderFactory;
    private JobBuilderFactory jobBuilderFactory;

    public JobConfiguration(
            StepBuilderFactory stepBuilderFactory,
            JobBuilderFactory jobBuilderFactory) {
        this.stepBuilderFactory = stepBuilderFactory;
        this.jobBuilderFactory = jobBuilderFactory;
    }

    @Bean
    @StepScope
    @Qualifier("incrementedItemReader")
    public ListItemReader<String> incrementedItemReader() {
        List<String> stringList = new ArrayList();
        for (int i = 0; i < 100; i++) {
            stringList.add(String.valueOf(i));
        }

        ListItemReader<String> reader = new ListItemReader<>(stringList);

        return reader;
    }

    @Bean
    @StepScope
    public SkipItemProcessor processor(@Value("#{jobParameters['skip']}") String skip) {
        SkipItemProcessor  processor = new SkipItemProcessor ();
        processor.setSkip(StringUtils.hasText(skip) && "processor".equals(skip));
        return processor;
    }

    @Bean
    @StepScope
    public SkipItemWriter writer(@Value("#{jobParameters['skip']}") String retry) {
        SkipItemWriter writer = new SkipItemWriter();
        writer.setSkip(StringUtils.hasText(retry) && "writer".equals(retry));
        return writer;
    }

    @Bean
    public Step skipStep() {
        return stepBuilderFactory.get("skipStep")
                .<String, String>chunk(10)
                .reader(incrementedItemReader())
                .processor(processor(null))
                .writer(writer(null))
                .faultTolerant()
                .skip(CustomRetryableException.class)
                .skipLimit(15)
                .build();
    }

    @Bean
    public Job skipJob(){
        return jobBuilderFactory.get("skipJob")
                .start(skipStep())
                .build();
    }
}
