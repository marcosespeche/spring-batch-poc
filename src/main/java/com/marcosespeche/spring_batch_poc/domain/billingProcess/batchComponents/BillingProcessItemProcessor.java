package com.marcosespeche.spring_batch_poc.domain.billingProcess.batchComponents;

import com.marcosespeche.spring_batch_poc.domain.billingProcess.billCalculator.IBillCalculator;
import com.marcosespeche.spring_batch_poc.entities.BillingProcessCustomer;
import com.marcosespeche.spring_batch_poc.entities.BillingProcessSimulation;
import com.marcosespeche.spring_batch_poc.entities.Customer;
import com.marcosespeche.spring_batch_poc.enums.BillingProcessCustomerState;
import com.marcosespeche.spring_batch_poc.enums.ServiceRequestState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.util.List;

@Slf4j
@Component
@StepScope
public class BillingProcessItemProcessor implements ItemProcessor<Customer, BillingProcessCustomer> {

    private final IBillCalculator billCalculator;
    private YearMonth period;

    @Autowired
    public BillingProcessItemProcessor(IBillCalculator billCalculator) {
        this.billCalculator = billCalculator;
    }

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        JobParameters jobParameters = stepExecution.getJobParameters();
        String periodString = jobParameters.getString("period");
        if (periodString != null) {
            this.period = YearMonth.parse(periodString);
        }
    }

    @Override
    public BillingProcessCustomer process(Customer item) throws Exception {
        List<ServiceRequestState> serviceStatesToBill = List.of(ServiceRequestState.DONE);
        BillingProcessSimulation simulation =  billCalculator.simulateCustomerBill(item, period, serviceStatesToBill);

        return BillingProcessCustomer.builder()
                .billingProcessSimulations(List.of(simulation))
                .customer(item)
                .state(BillingProcessCustomerState.PENDING_APPROVAL)
                .totalAmountCustomer(simulation.getTotalAmount())
                .build();
    }

}
