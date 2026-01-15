package com.marcosespeche.spring_batch_poc.domain.billingProcess;

import com.marcosespeche.spring_batch_poc.entities.BillingProcess;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.InvalidJobParametersException;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.launch.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BillingProcessScheduler {

    private final BillingProcessService billingProcessService;
    private final JobOperator jobOperator;
    private final Job billingProcessJob;

    @Autowired
    public BillingProcessScheduler(BillingProcessService billingProcessService, JobOperator jobOperator, Job billingProcessJob) {
        this.billingProcessService = billingProcessService;
        this.jobOperator = jobOperator;
        this.billingProcessJob = billingProcessJob;
    }

    // First day of the month
    @Scheduled(cron = "0 0 2 1 * ?")
    public void executeMonthlyBillingProcess() {
        log.info("Starting billing process");

        BillingProcess monthlyBillingProcess = billingProcessService.createMonthlyBillingProcessIfNotExists();

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("period", monthlyBillingProcess.getPeriod().toString())
                .addLong("billingProcessId", monthlyBillingProcess.getId())
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

        try {
            jobOperator.start(billingProcessJob, jobParameters);

        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 InvalidJobParametersException e) {
            log.error("Error during billing process");
            throw new RuntimeException(e.getMessage(), e);
        }

    }


}



