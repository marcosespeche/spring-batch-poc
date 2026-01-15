package com.marcosespeche.spring_batch_poc.config;

import com.marcosespeche.spring_batch_poc.domain.billingProcess.batchComponents.BillingProcessItemProcessor;
import com.marcosespeche.spring_batch_poc.domain.billingProcess.batchComponents.BillingProcessItemWriter;
import com.marcosespeche.spring_batch_poc.entities.BillingProcessCustomer;
import com.marcosespeche.spring_batch_poc.entities.Customer;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.JobOperatorFactoryBean;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.database.JpaPagingItemReader;
import org.springframework.batch.infrastructure.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.retry.RetryPolicy;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;


@Configuration
@EnableBatchProcessing(taskExecutorRef = "batchTaskExecutor")
//@EnableJdbcJobRepository
public class BatchConfig {

    @Autowired
    private BillingProcessItemProcessor billingProcessItemProcessor;

    @Autowired
    private BillingProcessItemWriter billingProcessItemWriter;

    @Bean
    public JpaPagingItemReader<Customer> customerItemReader(EntityManagerFactory entityManagerFactory) {
        return new JpaPagingItemReaderBuilder<Customer>()
                .name("customerItemReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT c FROM Customer c")
                .pageSize(5)
                .build();
    }

    @Bean
    public Step billingProcessStep(JobRepository jobRepository,
                                   PlatformTransactionManager transactionManager,
                                   ItemReader<Customer> reader) {

        int retryLimit = 3;
        RetryPolicy retryPolicy = RetryPolicy.builder()
                .maxRetries(retryLimit)
                .build();


        return new StepBuilder("billingProcessStep", jobRepository)
                .<Customer, BillingProcessCustomer>chunk(5).transactionManager(transactionManager)
                .reader(reader)
                .processor(billingProcessItemProcessor)
                .writer(billingProcessItemWriter)
                .retryPolicy(retryPolicy)
                .build();
    }

    @Bean
    public Job billingProcessJob(JobRepository jobRepository,
                                 Step billingProcessStep) {
        String jobName = "monthlyBillingProcessJob";
        return new JobBuilder(jobName, jobRepository)
                .start(billingProcessStep)
                .build();
    }

    @Bean(name = "batchTaskExecutor")
    public TaskExecutor taskExecutor() {
        return new SimpleAsyncTaskExecutor();
    }

    @Bean
    public JobOperatorFactoryBean jobOperator(JobRepository jobRepository, TaskExecutor taskExecutor) {
        JobOperatorFactoryBean jobOperatorFactoryBean = new JobOperatorFactoryBean();
        jobOperatorFactoryBean.setJobRepository(jobRepository);
        jobOperatorFactoryBean.setTaskExecutor(taskExecutor);
        return jobOperatorFactoryBean;
    }

}
