package com.marcosespeche.spring_batch_poc.domain.billingProcess.billCalculator;

import com.marcosespeche.spring_batch_poc.domain.serviceRequests.ServiceRequestService;
import com.marcosespeche.spring_batch_poc.entities.*;
import com.marcosespeche.spring_batch_poc.enums.AgreementState;
import com.marcosespeche.spring_batch_poc.enums.ServiceRequestState;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class BillCalculator implements IBillCalculator{

    private final ServiceRequestService serviceRequestService;

    @Autowired
    public BillCalculator(ServiceRequestService serviceRequestService) {
        this.serviceRequestService = serviceRequestService;
    }

    @Override
    @Transactional
    public BillingProcessSimulation simulateCustomerBill(Customer customer, YearMonth period, List<ServiceRequestState> serviceStatesToBill) {
        List<AgreementState> agreementStatesToBill = List.of(AgreementState.IN_COURSE, AgreementState.FINISHED);

        List<ServiceRequest> servicesToBill = serviceRequestService.findByCustomerIdAndPeriodAndSServiceStateAndAgreementState(
                customer,
                period,
                serviceStatesToBill,
                agreementStatesToBill
        );

        BillingProcessSimulation simulation = BillingProcessSimulation.builder()
                .simulatedAt(LocalDateTime.now())
                .totalAmount(0.0)
                .billingProcessAgreementList(new ArrayList<>())
                .build();

        Map<Agreement, List<ServiceRequest>> servicesByAgreement = servicesToBill.stream()
                .collect(Collectors.groupingBy(ServiceRequest::getAgreement));

        servicesByAgreement.forEach((agreement, servicesForAgreement) -> {

            BillingProcessAgreement billingProcessAgreement = BillingProcessAgreement.builder()
                    .agreement(agreement)
                    .totalAmountAgreement(0.0)
                    .billingProcessServiceRequestTypeList(new ArrayList<>())
                    .build();

            Map<ServiceRequestType, List<ServiceRequest>> servicesByType = servicesForAgreement.stream()
                    .collect(Collectors.groupingBy(ServiceRequest::getType));

            servicesByType.forEach((serviceRequestType, servicesForType) -> {

                Double hourlyFee = serviceRequestType.getHourlyFee();

                Double totalHours = servicesForType.stream()
                        .filter(service -> service.getRegisteredAt() != null && service.getFinishedAt() != null)
                        .mapToDouble(service -> Duration.between(service.getRegisteredAt(), service.getFinishedAt()).toMinutes() / 60.0)
                        .sum();

                Double totalAmount = hourlyFee * totalHours;

                BillingProcessServiceRequestType billingProcessSRT = BillingProcessServiceRequestType.builder()
                        .serviceRequestType(serviceRequestType)
                        .serviceRequests(servicesForType)
                        .totalHours(totalHours)
                        .hourlyFee(hourlyFee)
                        .totalAmountServiceRequestType(totalAmount)
                        .build();

                billingProcessAgreement.getBillingProcessServiceRequestTypeList().add(billingProcessSRT);
            });

            billingProcessAgreement.setTotalAmountAgreement(
                    billingProcessAgreement.getBillingProcessServiceRequestTypeList()
                            .stream()
                            .mapToDouble(BillingProcessServiceRequestType::getTotalAmountServiceRequestType)
                            .sum());

            simulation.getBillingProcessAgreementList().add(billingProcessAgreement);

        });

        simulation.setTotalAmount(simulation.getBillingProcessAgreementList()
                .stream()
                .mapToDouble(BillingProcessAgreement::getTotalAmountAgreement)
                .sum());

        return simulation;
    }
}
