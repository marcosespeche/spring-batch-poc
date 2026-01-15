package com.marcosespeche.spring_batch_poc.domain.serviceRequests;

import com.marcosespeche.spring_batch_poc.domain.agreements.AgreementService;
import com.marcosespeche.spring_batch_poc.domain.serviceRequestTypes.ServiceRequestTypeService;
import com.marcosespeche.spring_batch_poc.domain.serviceRequests.dtos.CreateServiceRequestDTO;
import com.marcosespeche.spring_batch_poc.domain.serviceRequests.dtos.ReadServiceRequestDTO;
import com.marcosespeche.spring_batch_poc.entities.Agreement;
import com.marcosespeche.spring_batch_poc.entities.Customer;
import com.marcosespeche.spring_batch_poc.entities.ServiceRequest;
import com.marcosespeche.spring_batch_poc.entities.ServiceRequestType;
import com.marcosespeche.spring_batch_poc.enums.AgreementState;
import com.marcosespeche.spring_batch_poc.enums.ServiceRequestState;
import com.marcosespeche.spring_batch_poc.mappers.ServiceRequestMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;

@Slf4j
@Service
public class ServiceRequestService {

    private final ServiceRequestRepository serviceRequestRepository;

    private final ServiceRequestMapper serviceRequestMapper;

    private final AgreementService agreementService;

    private final ServiceRequestTypeService serviceRequestTypeService;

    @Autowired
    public ServiceRequestService(ServiceRequestRepository serviceRequestRepository, ServiceRequestMapper serviceRequestMapper, AgreementService agreementService, ServiceRequestTypeService serviceRequestTypeService) {
        this.serviceRequestRepository = serviceRequestRepository;
        this.serviceRequestMapper = serviceRequestMapper;
        this.agreementService = agreementService;
        this.serviceRequestTypeService = serviceRequestTypeService;
    }

    @Transactional
    public Page<ReadServiceRequestDTO> findByCustomerAndProject(String filter, Pageable pageable) {
        Page<ServiceRequest> result = serviceRequestRepository.findByCustomerOrProject(filter, filter, pageable);
        return result.map(serviceRequestMapper::toReadServiceRequestDTO);
    }

    @Transactional
    public ReadServiceRequestDTO create(@Valid CreateServiceRequestDTO dto) {

        Agreement agreement = agreementService.findById(dto.agreementId());
        AgreementState agreementState = agreement.getState();

        // Only can create service requests for agreements 'in course' or 'accepted'
        if (!(agreementState.equals(AgreementState.IN_COURSE) || agreementState.equals(AgreementState.ACCEPTED))) {
            log.warn("Agreement with ID {} is not available for creating service requests", dto.agreementId());
            throw new IllegalArgumentException("Agreement is not available for creating service requests");
        }

        if (agreement.getState().equals(AgreementState.ACCEPTED)) {
            agreement.setState(AgreementState.IN_COURSE);
            agreementService.save(agreement);
        }

        ServiceRequestType type = serviceRequestTypeService.findById(dto.typeId());
        if (type.getSoftDeleteDate() != null) {
            log.warn("Service Request Type with ID {} is not active", dto.typeId());
            throw new IllegalArgumentException("Service Request Type not available");
        }

        ServiceRequest serviceRequest = ServiceRequest.builder()
                .type(type)
                .agreement(agreement)
                .state(ServiceRequestState.TO_DO)
                .description(dto.description())
                .registeredAt(LocalDateTime.now())
                .finishedAt(null)
                .build();

        serviceRequestRepository.save(serviceRequest);
        log.info("Service Request with ID {} created successfully", serviceRequest.getId());

        return serviceRequestMapper.toReadServiceRequestDTO(serviceRequest);
    }

    @Transactional
    public ReadServiceRequestDTO start(Long id) {
        ServiceRequest serviceRequest = findById(id);

        if (!serviceRequest.getState().equals(ServiceRequestState.TO_DO)) {
            log.warn("Service Request with ID {} already started", id);
            throw new IllegalArgumentException("Service Request already started");
        }

        serviceRequest.setState(ServiceRequestState.IN_PROGRESS);

        serviceRequestRepository.save(serviceRequest);
        log.info("Service Request with ID {} started", id);

        return serviceRequestMapper.toReadServiceRequestDTO(serviceRequest);
    }

    @Transactional
    public ReadServiceRequestDTO finish(Long id) {
        ServiceRequest serviceRequest = findById(id);

        if (!serviceRequest.getState().equals(ServiceRequestState.IN_PROGRESS)) {
            log.warn("Service Request with ID {} already finished", id);
            throw new IllegalArgumentException("Service Request not available for finishing");
        }

        serviceRequest.setState(ServiceRequestState.DONE);

        serviceRequestRepository.save(serviceRequest);
        log.info("Service Request with ID {} finished", id);

        return serviceRequestMapper.toReadServiceRequestDTO(serviceRequest);
    }

    @Transactional
    public void delete(Long id) {
        ServiceRequest serviceRequest = findById(id);

        if (!serviceRequest.getState().equals(ServiceRequestState.TO_DO)) {
            log.warn("Service Request with ID {} already started", id);
            throw new IllegalArgumentException("Service Request already started");
        }

        serviceRequestRepository.deleteById(id);
        log.info("Service Request with ID {} deleted", id);
    }

    @Transactional
    public List<ServiceRequest> findByCustomerIdAndPeriodAndSServiceStateAndAgreementState(
            Customer customer,
            YearMonth period,
            List<ServiceRequestState> serviceStatesToBill,
            List<AgreementState> agreementStates) {

        LocalDateTime startingDayOfMonth = period.atDay(1).atStartOfDay();
        LocalDateTime endingDayOfMonth = period.atEndOfMonth().atTime(LocalTime.MAX);
        return serviceRequestRepository.findByCustomerAndStateInAndPeriod(
                customer.getId(),
                startingDayOfMonth,
                endingDayOfMonth,
                serviceStatesToBill,
                agreementStates);
    }

    private ServiceRequest findById(Long id) {
        return serviceRequestRepository.findById(id).orElseThrow(() -> {
            log.warn("Service Request with ID {} not found", id);
            return new EntityNotFoundException("Service Request not found");
        });
    }
}
