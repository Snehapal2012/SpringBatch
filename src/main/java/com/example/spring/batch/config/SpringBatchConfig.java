package com.example.spring.batch.config;

import com.example.spring.batch.entity.Company;
import com.example.spring.batch.repository.CompanyRepository;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

@Configuration
@EnableBatchProcessing
@AllArgsConstructor
public class SpringBatchConfig {
    private JobBuilderFactory jobBuilderFactory;
    private StepBuilderFactory stepBuilderFactory;
    private CompanyRepository companyRepository;
    @Bean
    public FlatFileItemReader<Company> reader(){
       FlatFileItemReader<Company> itemReader=new FlatFileItemReader<>();
       itemReader.setResource(new FileSystemResource("src/main/resources/Sample500.csv"));
       itemReader.setName("csvReader");
       itemReader.setLinesToSkip(1);
       itemReader.setLineMapper(lineMapper());
       return itemReader;
    }


    private LineMapper<Company> lineMapper() {
        DefaultLineMapper<Company> lineMapper=new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer=new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("id","companyName","employeeName","description","leave");

        BeanWrapperFieldSetMapper<Company> fieldSetMapper=new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Company.class);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;

    }
    @Bean
    public CompanyProcessor processor(){
        return new CompanyProcessor();
    }
    @Bean
    public RepositoryItemWriter<Company> writer(){
        RepositoryItemWriter<Company> writer=new RepositoryItemWriter<>();
        writer.setRepository(companyRepository);
        writer.setMethodName("save");
        return writer;
    }
    @Bean
    public Step step1(){
        return stepBuilderFactory.get("csv-step").<Company,Company>chunk(10)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .taskExecutor(taskExecutor())
                .build();
    }
    @Bean
    public Job runJob(){
        return jobBuilderFactory.get("importCompanies")
                .flow(step1()).end().build();
    }
    @Bean
    public TaskExecutor taskExecutor(){
        SimpleAsyncTaskExecutor asyncTaskExecutor=new SimpleAsyncTaskExecutor();
        asyncTaskExecutor.setConcurrencyLimit(10);
        return asyncTaskExecutor;
    }
}
