package com.marcosespeche.spring_batch_poc.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class BillingProcessServiceRequestType extends BaseEntity {

    private Double totalAmountServiceRequestType;

    private Double totalHours;

    private Double hourlyFee;

    @ManyToOne
    @JoinColumn(name = "serviceRequestType", nullable = false)
    private ServiceRequestType serviceRequestType;

    @ManyToMany
    @JoinTable(
            name = "billingProcessServiceRequestType_ServiceRequest",
            joinColumns = @JoinColumn(name = "billingProcessServiceRequestTypeId"),
            inverseJoinColumns = @JoinColumn(name = "serviceRequestId")
    )
    private List<ServiceRequest> serviceRequests = new ArrayList<>();
}
