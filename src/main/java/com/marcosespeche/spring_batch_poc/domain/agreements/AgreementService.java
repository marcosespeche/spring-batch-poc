package com.marcosespeche.spring_batch_poc.domain.agreements;

import com.marcosespeche.spring_batch_poc.domain.agreements.dtos.CreateAgreementDTO;
import com.marcosespeche.spring_batch_poc.domain.agreements.dtos.ReadAgreementDTO;
import com.marcosespeche.spring_batch_poc.domain.agreements.dtos.UpdateAgreementDTO;
import com.marcosespeche.spring_batch_poc.domain.customers.CustomerService;
import com.marcosespeche.spring_batch_poc.domain.projects.ProjectService;
import com.marcosespeche.spring_batch_poc.entities.Agreement;
import com.marcosespeche.spring_batch_poc.entities.Customer;
import com.marcosespeche.spring_batch_poc.entities.Project;
import com.marcosespeche.spring_batch_poc.enums.AgreementState;
import com.marcosespeche.spring_batch_poc.mappers.AgreementMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;

@Slf4j
@Service
public class AgreementService {

    private final AgreementRepository agreementRepository;
    private final AgreementMapper agreementMapper;
    private final ProjectService projectService;

    @Autowired
    public AgreementService(AgreementRepository agreementRepository, AgreementMapper agreementMapper, CustomerService customerService, ProjectService projectService) {
        this.agreementRepository = agreementRepository;
        this.agreementMapper = agreementMapper;
        this.projectService = projectService;
    }

    @Transactional
    public Page<ReadAgreementDTO> findByCustomerOrProject(String filter, Pageable pageable) {
        Page<Agreement> result = agreementRepository.findByCustomerNameContainingIgnoreCaseOrProjectNameContainingIgnoreCaseOrderByAcceptedAtDesc(filter, filter, pageable);
        return result.map(agreementMapper::toReadAgreementDTO);
    }

    @Transactional
    public ReadAgreementDTO create(@Valid CreateAgreementDTO dto) {

        validatePeriods(dto.startingYear(), dto.endingYear(), dto.startingMonth(), dto.endingMonth());

        Project project = projectService.findActiveById(dto.projectId());

        Customer customer = project.getCustomer();

        if (customer.getSoftDeleteDate() != null) {
            log.warn("Customer with ID {} is not active", customer.getId());
            throw new IllegalArgumentException("Customer is not active");
        }

        if (agreementRepository.existsByCustomerIdAndStateNot(customer.getId(), AgreementState.FINISHED)) throw new IllegalArgumentException("Agreement already registered for that customer. Finish it or delete it first");

        Agreement agreement = Agreement.builder()
                .startingPeriod(YearMonth.of(dto.startingYear(), dto.startingMonth()))
                .endingPeriod(YearMonth.of(dto.endingYear(), dto.endingMonth()))
                .customer(customer)
                .project(project)
                .acceptedAt(null)
                .state(AgreementState.PROVISIONAL)
                .build();

        agreementRepository.save(agreement);
        log.info("Agreement with ID {} created successfully", agreement.getId());

        return agreementMapper.toReadAgreementDTO(agreement);
    }

    @Transactional
    public ReadAgreementDTO update(Long id, @Valid UpdateAgreementDTO dto) {
         Agreement agreement = findById(id);

         if (!agreement.getState().equals(AgreementState.PROVISIONAL)) {
             log.warn("Agreement with ID {} can not be modified because is already accepted", id);
             throw new IllegalArgumentException("Agreement already accepted");
         }

        validatePeriods(dto.startingYear(), dto.endingYear(), dto.startingMonth(), dto.endingMonth());

        agreement.setStartingPeriod(YearMonth.of(dto.startingYear(), dto.startingMonth()));
        agreement.setEndingPeriod(YearMonth.of(dto.endingYear(), dto.endingMonth()));

        agreementRepository.save(agreement);
        log.info("Agreement with ID {} updated successfully", id);

        return agreementMapper.toReadAgreementDTO(agreement);
    }

    @Transactional
    public void delete(Long id) {
        Agreement agreement = findById(id);

        if (!agreement.getState().equals(AgreementState.PROVISIONAL)) {
            log.warn("Agreement with ID {} can not be deleted because is already accepted", id);
            throw new IllegalArgumentException("Agreement already accepted");
        }

        agreementRepository.deleteById(id);
    }

    @Transactional
    public ReadAgreementDTO accept(Long id) {
        Agreement agreement = findById(id);

        YearMonth actualPeriod = YearMonth.now();

        if (actualPeriod.isAfter(agreement.getStartingPeriod())) throw new IllegalArgumentException("Starting period already passed");

        if (!agreement.getState().equals(AgreementState.PROVISIONAL)) {
            log.warn("Agreement with ID {} can not be accepted because is already accepted", id);
            throw new IllegalArgumentException("Agreement already accepted");
        }

        agreement.setState(AgreementState.ACCEPTED);

        agreementRepository.save(agreement);
        log.info("Agreement with ID {} accepted successfully", id);

        return agreementMapper.toReadAgreementDTO(agreement);
    }

    @Transactional
    public Agreement findById(Long id) {
        return agreementRepository.findById(id).orElseThrow(() -> {
            log.warn("Agreement with ID {} can not be found", id);
            return new EntityNotFoundException("Agreement not found");
        });
    }

    private static void validatePeriods(int startingYear, int endingYear, int startingMonth, int endingMonth) {
        if (startingYear > endingYear) throw new IllegalArgumentException("Starting year can not be later than ending year");
        if (startingYear == endingYear && startingMonth >= endingMonth) throw new IllegalArgumentException("Starting month can not be later than ending month");
    }

}
