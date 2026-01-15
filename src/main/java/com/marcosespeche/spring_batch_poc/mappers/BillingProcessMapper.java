package com.marcosespeche.spring_batch_poc.mappers;

import com.marcosespeche.spring_batch_poc.domain.billingProcess.dtos.ReadBillingProcessDTO;
import com.marcosespeche.spring_batch_poc.entities.BillingProcess;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BillingProcessMapper {

    ReadBillingProcessDTO toReadBillingProcessDTO(BillingProcess billingProcess);
}
