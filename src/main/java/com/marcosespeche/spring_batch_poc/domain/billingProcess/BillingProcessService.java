package com.marcosespeche.spring_batch_poc.domain.billingProcess;

import com.marcosespeche.spring_batch_poc.entities.BillingProcess;
import com.marcosespeche.spring_batch_poc.enums.BillingProcessState;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Optional;

@Service
public class BillingProcessService {

    private final BillingProcessRepository billingProcessRepository;

    @Autowired
    public BillingProcessService(BillingProcessRepository billingProcessRepository) {
        this.billingProcessRepository = billingProcessRepository;
    }

    @Transactional
    public BillingProcess createMonthlyBillingProcessIfNotExists() {
        YearMonth lastMonth = YearMonth.now().minusMonths(1);

        Optional<BillingProcess> billingProcessOptional = billingProcessRepository.existsByPeriod(lastMonth);

        if (billingProcessOptional.isEmpty()) {

            BillingProcess billingProcess = BillingProcess.builder()
                    .totalAmountBillingProcess(null)
                    .totalAmountBillingProcess(0.0)
                    .period(lastMonth)
                    .registeredAt(LocalDateTime.now())
                    .state(BillingProcessState.REGISTERED)
                    .build();

            billingProcessRepository.save(billingProcess);

            return billingProcess;
        }

        return billingProcessOptional.get();
    }
}
