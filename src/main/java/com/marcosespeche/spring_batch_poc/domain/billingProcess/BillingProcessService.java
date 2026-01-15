package com.marcosespeche.spring_batch_poc.domain.billingProcess;

import com.marcosespeche.spring_batch_poc.domain.billingProcess.dtos.ReadBillingProcessDTO;
import com.marcosespeche.spring_batch_poc.entities.*;
import com.marcosespeche.spring_batch_poc.enums.BillingProcessState;
import com.marcosespeche.spring_batch_poc.mappers.BillingProcessMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Optional;

@Slf4j
@Service
public class BillingProcessService {

    private final BillingProcessRepository billingProcessRepository;
    private final BillingProcessMapper billingProcessMapper;

    @Autowired
    public BillingProcessService(BillingProcessRepository billingProcessRepository, BillingProcessMapper billingProcessMapper) {
        this.billingProcessRepository = billingProcessRepository;
        this.billingProcessMapper = billingProcessMapper;
    }

    @Transactional
    public Page<ReadBillingProcessDTO> findAll(Pageable pageable) {
        Page<BillingProcess> result = billingProcessRepository.findAll(pageable);
        return result.map(billingProcessMapper::toReadBillingProcessDTO);
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

    @Transactional
    public void save(BillingProcess billingProcess) {
        this.billingProcessRepository.save(billingProcess);
    }

    @Transactional
    public BillingProcess findById(Long id) {
        return billingProcessRepository.findById(id).orElseThrow(() -> {
           log.error("Billing process with ID {} was not found", id);
           return new EntityNotFoundException("Billing process not found");
        });
    }

}
