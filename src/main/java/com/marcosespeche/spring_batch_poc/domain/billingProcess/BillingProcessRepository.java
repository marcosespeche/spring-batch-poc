package com.marcosespeche.spring_batch_poc.domain.billingProcess;

import com.marcosespeche.spring_batch_poc.entities.BillingProcess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.Optional;

@Repository
public interface BillingProcessRepository extends JpaRepository<BillingProcess, Long> {

    Optional<BillingProcess> existsByPeriod(YearMonth period);
}
