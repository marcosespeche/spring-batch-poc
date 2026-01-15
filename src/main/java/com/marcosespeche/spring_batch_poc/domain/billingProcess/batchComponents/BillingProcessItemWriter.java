package com.marcosespeche.spring_batch_poc.domain.billingProcess.batchComponents;

import com.marcosespeche.spring_batch_poc.domain.billingProcess.BillingProcessService;
import com.marcosespeche.spring_batch_poc.entities.BillingProcess;
import com.marcosespeche.spring_batch_poc.entities.BillingProcessCustomer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@StepScope
@Slf4j
public class BillingProcessItemWriter implements ItemWriter<BillingProcessCustomer> {

    private final BillingProcessService billingProcessService;
    private Long billingProcessId;

    @Autowired
    public BillingProcessItemWriter(BillingProcessService billingProcessService) {
        this.billingProcessService = billingProcessService;
    }

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        JobParameters jobParameters = stepExecution.getJobParameters();
        Long billingProcessId = jobParameters.getLong("billingProcessId");
        if (billingProcessId != null) {
            this.billingProcessId = billingProcessId;
        }
    }

    @Override
    public void write(Chunk<? extends BillingProcessCustomer> chunk) throws Exception {

        List<BillingProcessCustomer> billingProcessCustomers = new ArrayList<>(chunk.getItems());

        BillingProcess billingProcess = billingProcessService.findById(billingProcessId);

        if (billingProcess.getBillingProcessCustomerList() == null) {
            billingProcess.setBillingProcessCustomerList(new ArrayList<>(billingProcessCustomers));
        } else {
            billingProcess.getBillingProcessCustomerList().addAll(billingProcessCustomers);
        }

        billingProcessService.save(billingProcess);
    }
}
