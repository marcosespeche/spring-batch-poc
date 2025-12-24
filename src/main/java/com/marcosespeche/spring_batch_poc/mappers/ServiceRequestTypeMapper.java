package com.marcosespeche.spring_batch_poc.mappers;

import com.marcosespeche.spring_batch_poc.domain.serviceRequestTypes.dtos.ReadServiceRequestTypeDTO;
import com.marcosespeche.spring_batch_poc.entities.ServiceRequestType;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ServiceRequestTypeMapper {

    ReadServiceRequestTypeDTO toReadServiceRequestDTO(ServiceRequestType entity);
}
