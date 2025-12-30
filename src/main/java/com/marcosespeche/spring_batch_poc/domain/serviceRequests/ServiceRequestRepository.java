package com.marcosespeche.spring_batch_poc.domain.serviceRequests;

import com.marcosespeche.spring_batch_poc.entities.ServiceRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
}
