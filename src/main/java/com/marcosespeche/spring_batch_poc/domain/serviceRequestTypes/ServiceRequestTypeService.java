package com.marcosespeche.spring_batch_poc.domain.serviceRequestTypes;

import com.marcosespeche.spring_batch_poc.domain.serviceRequestTypes.dtos.CreateServiceRequestTypeDTO;
import com.marcosespeche.spring_batch_poc.domain.serviceRequestTypes.dtos.ReadServiceRequestTypeDTO;
import com.marcosespeche.spring_batch_poc.domain.serviceRequestTypes.dtos.UpdateServiceRequestTypeDTO;
import com.marcosespeche.spring_batch_poc.entities.ServiceRequestType;
import com.marcosespeche.spring_batch_poc.mappers.ServiceRequestTypeMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ServiceRequestTypeService {

    private final ServiceRequestTypeRepository serviceRequestTypeRepository;
    private final ServiceRequestTypeMapper serviceRequestTypeMapper;

    @Autowired
    public ServiceRequestTypeService(ServiceRequestTypeRepository serviceRequestTypeRepository, ServiceRequestTypeMapper serviceRequestTypeMapper) {
        this.serviceRequestTypeRepository = serviceRequestTypeRepository;
        this.serviceRequestTypeMapper = serviceRequestTypeMapper;
    }

    @Transactional
    public Page<ReadServiceRequestTypeDTO> getAllPaged(String filter, Pageable pageable) {
        return serviceRequestTypeRepository.findByNameContainingIgnoreCase(filter, pageable)
                .map(serviceRequestTypeMapper::toReadServiceRequestDTO
                );
    }

    @Transactional
    public List<ReadServiceRequestTypeDTO> getAllActive(String filter) {
        return serviceRequestTypeRepository.findByNameContainingIgnoreCaseAndSoftDeleteDateIsNull(filter)
                .stream().map(serviceRequestTypeMapper::toReadServiceRequestDTO)
                .toList();
    }

    @Transactional
    public ReadServiceRequestTypeDTO create(@Valid CreateServiceRequestTypeDTO dto) {

        if (serviceRequestTypeRepository.existsByName(dto.name())) throw new IllegalArgumentException("Service Request Type's name already exists");

        ServiceRequestType serviceRequestType = ServiceRequestType.builder()
                .name(dto.name())
                .description(dto.description())
                .hourlyFee(dto.hourlyFee())
                .softDeleteDate(null)
                .build();

        serviceRequestTypeRepository.save(serviceRequestType);

        return serviceRequestTypeMapper.toReadServiceRequestDTO(serviceRequestType);
    }

    @Transactional
    public ReadServiceRequestTypeDTO update(Long id, @Valid UpdateServiceRequestTypeDTO dto) {

        ServiceRequestType serviceRequestType = serviceRequestTypeRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Service Request Type not found"));

        if (!(serviceRequestType.getName().equalsIgnoreCase(dto.name())) && serviceRequestTypeRepository.existsByName(dto.name()))
            throw new IllegalArgumentException("Service Request Type's name already exists");

        serviceRequestType.setName(dto.name());
        serviceRequestType.setDescription(dto.description());
        serviceRequestType.setHourlyFee(dto.hourlyFee());

        serviceRequestTypeRepository.save(serviceRequestType);

        return serviceRequestTypeMapper.toReadServiceRequestDTO(serviceRequestType);
    }

    @Transactional
    public ReadServiceRequestTypeDTO deleteOrRestore(Long id) {
        ServiceRequestType serviceRequestType = serviceRequestTypeRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Service Request Type not found"));

        if (serviceRequestType.getSoftDeleteDate() == null) {
            serviceRequestType.setSoftDeleteDate(LocalDateTime.now());
        } else {
            serviceRequestType.setSoftDeleteDate(null);
        }

        serviceRequestTypeRepository.save(serviceRequestType);

        return serviceRequestTypeMapper.toReadServiceRequestDTO(serviceRequestType);
    }

}
