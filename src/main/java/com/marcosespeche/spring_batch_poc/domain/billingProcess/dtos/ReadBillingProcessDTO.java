package com.marcosespeche.spring_batch_poc.domain.billingProcess.dtos;

import com.marcosespeche.spring_batch_poc.enums.BillingProcessState;

import java.time.LocalDateTime;
import java.time.YearMonth;

public record ReadBillingProcessDTO(

        Long id,

        YearMonth period,

        LocalDateTime registeredAt,

        Double totalAmountBillingProcess,

        BillingProcessState state
) {
}
