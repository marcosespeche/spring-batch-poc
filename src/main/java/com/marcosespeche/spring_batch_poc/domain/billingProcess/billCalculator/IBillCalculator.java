package com.marcosespeche.spring_batch_poc.domain.billingProcess.billCalculator;

import com.marcosespeche.spring_batch_poc.entities.BillingProcessSimulation;
import com.marcosespeche.spring_batch_poc.entities.Customer;
import com.marcosespeche.spring_batch_poc.enums.ServiceRequestState;

import java.time.YearMonth;
import java.util.List;

public interface IBillCalculator {

    BillingProcessSimulation simulateCustomerBill(Customer customer, YearMonth period, List<ServiceRequestState> serviceStatesToBill);
}
