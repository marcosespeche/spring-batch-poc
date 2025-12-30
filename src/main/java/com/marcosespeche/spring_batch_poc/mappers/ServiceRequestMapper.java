package com.marcosespeche.spring_batch_poc.mappers;

import com.marcosespeche.spring_batch_poc.domain.serviceRequests.dtos.ReadServiceRequestDTO;
import com.marcosespeche.spring_batch_poc.entities.ServiceRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ServiceRequestMapper {

    @Mapping(target = "projectName", source = "agreement.project.name")
    @Mapping(target = "customerName", source = "agreement.customer.name")
    @Mapping(target = "type", source = "type.name")
    ReadServiceRequestDTO toReadServiceRequestDTO(ServiceRequest serviceRequest);
}
