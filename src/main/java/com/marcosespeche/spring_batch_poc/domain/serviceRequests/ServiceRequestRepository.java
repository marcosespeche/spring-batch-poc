package com.marcosespeche.spring_batch_poc.domain.serviceRequests;

import com.marcosespeche.spring_batch_poc.entities.ServiceRequest;
import com.marcosespeche.spring_batch_poc.enums.AgreementState;
import com.marcosespeche.spring_batch_poc.enums.ServiceRequestState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {


    @Query("""
            SELECT sr FROM ServiceRequest sr
            JOIN sr.agreement a
            JOIN a.project p
            JOIN a.customer c
            WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :customerName, '%')) OR LOWER(p.name) LIKE LOWER(CONCAT('%', :projectName, '%'))
            """)
    Page<ServiceRequest> findByCustomerOrProject(@Param("customerName") String customerName, @Param("projectName") String projectName, Pageable pageable);


    @Query("""
            SELECT sr FROM ServiceRequest sr
            JOIN sr.agreement a
            JOIN a.customer c
            WHERE
            c.id = :customerId AND
            a.state IN :agreementStateList AND
            sr.finishedAt IS NOT NULL AND
            sr.finishedAt <= :endingDay AND sr.registeredAt >= :startingDay
            AND sr.state IN :serviceStateList
            """)
    List<ServiceRequest> findByCustomerAndStateInAndPeriod(
            @Param("customerId") Long customerId,
            @Param("startingDay") LocalDateTime startingDay,
            @Param("endingDay") LocalDateTime endingDay,
            @Param("serviceStateList") List<ServiceRequestState> serviceStateList,
            @Param("agreementStateList")List<AgreementState> agreementStateList);

}
