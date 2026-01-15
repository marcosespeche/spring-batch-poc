package com.marcosespeche.spring_batch_poc.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

@TestConfiguration
public class SyncTaskExecutorConfiguration {

    @Bean(name = "batchTaskExecutor")
    @Primary
    public TaskExecutor syncBatchTaskExecutor() {
        return new SyncTaskExecutor();
    }
}