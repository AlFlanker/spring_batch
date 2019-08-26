package com.gmail.alexflanker89;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.*;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@RequiredArgsConstructor
@ShellComponent
public class TranslationCommands {
    private final JobLauncher jobLauncher;
    private final JobBuilderFactory jobBuilderFactory;
    private final JobRepository jobRepository;
    private final JobExplorer jobExplorer;
    private final JobOperator jobOperator;
    @Qualifier("importUserStep")
    private final Step step;


    @ShellMethod(value = "start work", key = "start")
    public void start(String start) {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("type", "manualImportUserJob")
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            JobExecution run = jobLauncher.run(importUserJob(step), jobParameters);
            run.getExitStatus().getExitCode();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @ShellMethod(value = "Метод для перезапуска Job", key = "restart")
    public void restart(String restart) throws JobParametersInvalidException, JobRestartException, JobInstanceAlreadyCompleteException, NoSuchJobExecutionException, NoSuchJobException, JobExecutionAlreadyRunningException {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("type", "manualImportUserJob")
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();
        jobLauncher.run(importUserJob(step), jobParameters);
    }

    @Bean
    public Job importUserJob(Step step) {
        return jobBuilderFactory
                .get("importUserJob" )
                .flow(step)
                .end()
                .build();
    }


}
