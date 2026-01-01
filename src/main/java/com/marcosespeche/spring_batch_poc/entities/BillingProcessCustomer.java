package com.marcosespeche.spring_batch_poc.entities;

import com.marcosespeche.spring_batch_poc.enums.BillingProcessCustomerState;
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
public class BillingProcessCustomer extends BaseEntity {

    private Double totalAmountCustomer;

    private BillingProcessCustomerState state;

    @ManyToOne
    @JoinColumn(nullable = false, name = "customer")
    private Customer customer;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "billingProcessCustomer")
    private List<BillingProcessSimulation> billingProcessSimulations = new ArrayList<>();

}
