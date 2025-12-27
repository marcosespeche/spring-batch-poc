package com.marcosespeche.spring_batch_poc.domain.projects;

import com.marcosespeche.spring_batch_poc.domain.customers.CustomerService;
import com.marcosespeche.spring_batch_poc.domain.projects.dtos.ReadProjectDTO;
import com.marcosespeche.spring_batch_poc.entities.Customer;
import com.marcosespeche.spring_batch_poc.entities.Project;
import com.marcosespeche.spring_batch_poc.mappers.ProjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    private final CustomerService customerService;

    private final ProjectMapper projectMapper;

    @Autowired
    public ProjectService(ProjectRepository projectRepository, CustomerService customerService, ProjectMapper projectMapper) {
        this.projectRepository = projectRepository;
        this.customerService = customerService;
        this.projectMapper = projectMapper;
    }

    @Transactional
    public Page<ReadProjectDTO> findByCustomer(String filter, Pageable pageable, Long customerId) {

        Customer customer = customerService.findById(customerId);

        Page<Project> projects = projectRepository.findByCustomerIdAndNameContainingIgnoreCase(customerId, filter, pageable);

        return projects.map(projectMapper::toReadProjectDTO);
    }

    @Transactional
    public List<ReadProjectDTO> findActiveByCustomer(String filter, Long customerId) {

        Customer customer = customerService.findById(customerId);

        List<Project> projects = projectRepository.findByCustomerIdAndNameContainingIgnoreCaseAndSoftDeleteDateIsNull(customerId, filter);

        return projects.stream()
                .map(projectMapper::toReadProjectDTO)
                .toList();
    }
}
