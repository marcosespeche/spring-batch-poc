package com.marcosespeche.spring_batch_poc.domain.agreements;

import com.marcosespeche.spring_batch_poc.entities.Agreement;
import com.marcosespeche.spring_batch_poc.enums.AgreementState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgreementRepository extends JpaRepository<Agreement, Long> {

    Page<Agreement> findByCustomerNameContainingIgnoreCaseOrProjectNameContainingIgnoreCaseOrderByAcceptedAtDesc(String customerName, String projectName, Pageable pageable);

    boolean existsByCustomerIdAndStateNot(Long customerId, AgreementState state);

}
