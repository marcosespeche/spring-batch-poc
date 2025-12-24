package com.marcosespeche.spring_batch_poc.domain.serviceRequestTypes;

import com.marcosespeche.spring_batch_poc.entities.ServiceRequestType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRequestTypeRepository extends JpaRepository<ServiceRequestType, Long> {

    boolean existsByName(String name);

    List<ServiceRequestType> findByNameContainingIgnoreCaseAndSoftDeleteDateIsNull(String name);

    Page<ServiceRequestType> findByNameContainingIgnoreCase(String name, Pageable pageable);

}
