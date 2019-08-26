package com.gmail.alexflanker89;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.h2.H2ConsoleProperties;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableBatchProcessing
@EnableMongoRepositories
@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class);
    }
}
