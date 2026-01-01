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
public class BillingProcessAgreement extends BaseEntity {

    private Double totalAmountAgreement;

    @ManyToOne
    @JoinColumn(nullable = false, name = "agreement")
    private Agreement agreement;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "billingProcessAgreement")
    private List<BillingProcessServiceRequestType> billingProcessServiceRequestTypeList = new ArrayList<>();

}
