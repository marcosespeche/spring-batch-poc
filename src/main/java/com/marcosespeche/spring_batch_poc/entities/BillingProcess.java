package com.marcosespeche.spring_batch_poc.entities;

import com.marcosespeche.spring_batch_poc.enums.BillingProcessState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class BillingProcess extends BaseEntity{

    private LocalDateTime registeredAt;

    private YearMonth period;

    private Double totalAmountBillingProcess;

    private BillingProcessState state;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "billingProcess", nullable = false)
    private List<BillingProcessCustomer> billingProcessCustomerList = new ArrayList<>();
}
