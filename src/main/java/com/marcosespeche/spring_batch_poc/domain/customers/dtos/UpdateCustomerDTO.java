package com.marcosespeche.spring_batch_poc.domain.customers.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateCustomerDTO(

        @Schema(
                description = "New customer name",
                example = "Software Factory 123"
        )
        @Size(min = 4, max = 20, message = "Customer name must contain between 4 and 20 characters")
        @NotBlank(message = "Customer name is required")
        String name,

        @Schema(
                description = "New customer email",
                example = "software_factory_123@gmail.com")
        @Email(message = "Invalid email format")
        @NotBlank(message = "Email is required")
        String email
) {
}
