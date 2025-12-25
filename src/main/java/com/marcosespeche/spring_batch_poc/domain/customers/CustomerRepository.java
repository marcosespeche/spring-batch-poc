package com.marcosespeche.spring_batch_poc.domain.customers;

import com.marcosespeche.spring_batch_poc.entities.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Page<Customer> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Customer> findByNameContainingIgnoreCaseAndSoftDeleteDateIsNull(String name, Pageable pageable);

    boolean existsByName(String name);
}
