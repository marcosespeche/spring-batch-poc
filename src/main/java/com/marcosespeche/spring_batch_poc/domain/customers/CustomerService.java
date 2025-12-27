package com.marcosespeche.spring_batch_poc.domain.customers;

import com.marcosespeche.spring_batch_poc.domain.customers.dtos.CreateCustomerDTO;
import com.marcosespeche.spring_batch_poc.domain.customers.dtos.ReadCustomerDTO;
import com.marcosespeche.spring_batch_poc.domain.customers.dtos.UpdateCustomerDTO;
import com.marcosespeche.spring_batch_poc.entities.Customer;
import com.marcosespeche.spring_batch_poc.mappers.CustomerMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;

    private final CustomerMapper customerMapper;

    @Autowired
    public CustomerService(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    @Transactional
    public Page<ReadCustomerDTO> getAllPaged(String filter, Pageable pageable) {
        Page<Customer> customers = customerRepository.findByNameContainingIgnoreCase(filter, pageable);
        return customers.map(customerMapper::toReadCustomerDTO);
    }

    @Transactional
    public Page<ReadCustomerDTO> getAllActivePaged(String filter, Pageable pageable) {
        Page<Customer> customers = customerRepository.findByNameContainingIgnoreCaseAndSoftDeleteDateIsNull(filter, pageable);
        return customers.map(customerMapper::toReadCustomerDTO);
    }

    @Transactional
    public ReadCustomerDTO create(@Valid CreateCustomerDTO dto) {

        validateDuplicatedName(dto.name());
        validateDuplicatedEmail(dto.email());

        Customer customer = Customer.builder()
                .name(dto.name())
                .email(dto.email())
                .softDeleteDate(null)
                .build();

        customerRepository.save(customer);

        log.info("Customer with ID {} created successfully", customer.getId());

        return customerMapper.toReadCustomerDTO(customer);
    }

    @Transactional
    public ReadCustomerDTO update(Long id, @Valid UpdateCustomerDTO dto) {

        Customer customer = findById(id);

        if (!customer.getName().equalsIgnoreCase(dto.name())) validateDuplicatedName(dto.name());

        if (!customer.getEmail().equalsIgnoreCase(dto.email())) validateDuplicatedEmail(dto.email());

        customer.setName(dto.name());
        customer.setEmail(dto.email());

        customerRepository.save(customer);

        log.info("Customer with ID {} updated successfully", customer.getId());

        return customerMapper.toReadCustomerDTO(customer);
    }

    @Transactional
    public ReadCustomerDTO deleteOrRestore(Long id) {

        Customer customer = findById(id);

        String action;

        if (customer.getSoftDeleteDate() == null) {
            action = "deleted";
            customer.setSoftDeleteDate(LocalDateTime.now());
        } else {
            action = "restored";
            customer.setSoftDeleteDate(null);
        }

        customerRepository.save(customer);

        log.info("Customer with ID {} {} successfully", id, action);

        return customerMapper.toReadCustomerDTO(customer);
    }

    @Transactional
    public Customer findById(Long id) {
        return customerRepository.findById(id).orElseThrow(() -> {
            log.warn("Customer with ID {} cannot be found", id);
            return new EntityNotFoundException("Customer not found");
        });
    }

    private void validateDuplicatedName(String name) {
        if (customerRepository.existsByName(name)) {
            log.warn("Cannot create customer with duplicated name");
            throw new IllegalArgumentException("Customer with that name already exists");
        }
    }

    private void validateDuplicatedEmail(String email) {
        if (customerRepository.existsByEmail(email)) {
            log.warn("Cannot create customer with duplicated email");
            throw new IllegalArgumentException("Customer with that email already exists");
        }
    }


}
