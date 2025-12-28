package com.marcosespeche.spring_batch_poc.mappers;

import com.marcosespeche.spring_batch_poc.domain.agreements.dtos.ReadAgreementDTO;
import com.marcosespeche.spring_batch_poc.entities.Agreement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AgreementMapper {

    @Mapping(source = "project.name", target = "projectName")
    @Mapping(source = "customer.name", target = "customerName")
    ReadAgreementDTO toReadAgreementDTO(Agreement agreement);
}
