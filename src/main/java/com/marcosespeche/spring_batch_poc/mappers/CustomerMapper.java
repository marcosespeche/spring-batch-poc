package com.marcosespeche.spring_batch_poc.mappers;

import com.marcosespeche.spring_batch_poc.domain.customers.dtos.ReadCustomerDTO;
import com.marcosespeche.spring_batch_poc.entities.Customer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    ReadCustomerDTO toReadCustomerDTO(Customer customer);
}
