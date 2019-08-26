package com.gmail.alexflanker89.batch;

import com.gmail.alexflanker89.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.SimpleStepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.MongoItemReader;
import org.springframework.batch.item.data.builder.MongoItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Configuration
public class BatchConfig {

    private final StepBuilderFactory stepBuilderFactory;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public MongoItemReader<User> mongoReader(MongoOperations mongoOperations){
        return new MongoItemReaderBuilder<User>()
                .template(mongoOperations)
                .name("mongoUserItemReader")
                .jsonQuery("{}")
                .targetType(User.class)
                .sorts(new HashMap<>())
                .build();
    }

    @Bean
    public ItemProcessor processor() {
        return (ItemProcessor<User, User>) person -> person;
    }

    @Bean
    public ItemWriter<User> writer(JdbcOperations jdbcOperations){
        return new H2Writer(jdbcOperations);
    }



    @SuppressWarnings("unchecked")
    @Bean(name = "importUserStep")
    public Step step1(ItemWriter<User> writer, MongoItemReader reader, ItemProcessor itemProcessor) {
        SimpleStepBuilder step1 = stepBuilderFactory.get("step1")
                .chunk(5)
                .reader(reader)
                .processor(itemProcessor)
                .writer(writer);
        step1.allowStartIfComplete(true);
        return step1
                .listener(new ItemReadListener() {
                    public void beforeRead() { log.info("Начало чтения"); }
                    public void afterRead(Object o) { log.info("Конец чтения"); }
                    public void onReadError(Exception e) { log.info("Ошибка чтения"); }
                })
                .listener(new ItemWriteListener() {
                    public void beforeWrite(List list) { log.info("Начало записи"); }
                    public void afterWrite(List list) { log.info("Конец записи"); }
                    public void onWriteError(Exception e, List list) { log.info("Ошибка записи"); }
                })
                .listener(new ItemProcessListener() {
                    public void beforeProcess(Object o) {log.info("Начало обработки");}
                    public void afterProcess(Object o, Object o2) {log.info("Конец обработки");}
                    public void onProcessError(Object o, Exception e) {log.info("Ошбка обработки");}
                })
                .listener(new ChunkListener() {
                    public void beforeChunk(ChunkContext chunkContext) {log.info("Начало пачки");}
                    public void afterChunk(ChunkContext chunkContext) {log.info("Конец пачки");}
                    public void afterChunkError(ChunkContext chunkContext) {log.info("Ошибка пачки");}
                })
                .build();
    }

    @Bean
    public JobRepository getJobRepository() throws Exception {
        MapJobRepositoryFactoryBean factory = new MapJobRepositoryFactoryBean();
        factory.setTransactionManager(this.transactionManager);
        return factory.getObject();
    }



}
