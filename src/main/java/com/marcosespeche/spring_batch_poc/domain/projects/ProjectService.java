package com.marcosespeche.spring_batch_poc.domain.projects;

import com.marcosespeche.spring_batch_poc.domain.customers.CustomerService;
import com.marcosespeche.spring_batch_poc.domain.projects.dtos.CreateProjectDTO;
import com.marcosespeche.spring_batch_poc.domain.projects.dtos.ReadProjectDTO;
import com.marcosespeche.spring_batch_poc.domain.projects.dtos.UpdateProjectDTO;
import com.marcosespeche.spring_batch_poc.entities.Customer;
import com.marcosespeche.spring_batch_poc.entities.Project;
import com.marcosespeche.spring_batch_poc.mappers.ProjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
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

        customerService.findById(customerId);

        Page<Project> projects = projectRepository.findByCustomerIdAndNameContainingIgnoreCase(customerId, filter, pageable);

        return projects.map(projectMapper::toReadProjectDTO);
    }

    @Transactional
    public List<ReadProjectDTO> findActiveByCustomer(String filter, Long customerId) {

        customerService.findById(customerId);

        List<Project> projects = projectRepository.findByCustomerIdAndNameContainingIgnoreCaseAndSoftDeleteDateIsNull(customerId, filter);

        return projects.stream()
                .map(projectMapper::toReadProjectDTO)
                .toList();
    }

    @Transactional
    public ReadProjectDTO create(CreateProjectDTO dto) {

        Customer customer = customerService.findActiveById(dto.customerId());

        validateDuplicatedName(dto.name(), dto.customerId());

        Project project = Project.builder()
                .name(dto.name())
                .description(dto.description())
                .customer(customer)
                .softDeleteDate(null)
                .build();

        projectRepository.save(project);
        log.info("Project with ID {} created successfully", project.getId());

        return projectMapper.toReadProjectDTO(project);
    }

    @Transactional
    public ReadProjectDTO update(Long projectId, UpdateProjectDTO dto) {

        Project project = findById(projectId);

        if (!dto.name().equalsIgnoreCase(project.getName())) validateDuplicatedName(dto.name(), project.getCustomer().getId());

        project.setName(dto.name());
        project.setDescription(dto.description());

        projectRepository.save(project);
        log.info("Project with ID {} updated successfully", projectId);

        return projectMapper.toReadProjectDTO(project);
    }

    @Transactional
    public ReadProjectDTO deleteOrRestore(Long projectId) {

        Project project = findById(projectId);
        String action;

        if (project.getSoftDeleteDate() == null) {
            project.setSoftDeleteDate(LocalDateTime.now());
            action = "deleted";
        } else {
            project.setSoftDeleteDate(null);
            action = "restored";
        }

        projectRepository.save(project);
        log.info("Project with ID {} {}", projectId, action);

        return projectMapper.toReadProjectDTO(project);
    }

    @Transactional
    public Project findById(Long id) {
        return projectRepository.findById(id).orElseThrow(() -> {
            log.warn("Project with ID {} cannot be found", id);
            return new EntityNotFoundException("Project not found");
        });
    }

    private void validateDuplicatedName(String name, Long customerId) {

        if (projectRepository.existsByNameAndCustomerId(name, customerId)) {
            log.warn("Cannot create project for Customer with ID {} because of duplicated project name", customerId);
            throw new IllegalArgumentException("Project with that name already exists for that customer");
        }

    }
}
