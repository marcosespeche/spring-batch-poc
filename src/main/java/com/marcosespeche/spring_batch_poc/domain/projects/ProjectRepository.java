package com.marcosespeche.spring_batch_poc.domain.projects;

import com.marcosespeche.spring_batch_poc.entities.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    Page<Project> findByCustomerIdAndNameContainingIgnoreCase(Long customerId, String filter, Pageable pageable);

    List<Project> findByCustomerIdAndNameContainingIgnoreCaseAndSoftDeleteDateIsNull(Long customerId, String filter);

    boolean existsByNameAndCustomerId(String name, Long customerId);
}
