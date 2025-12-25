package com.marcosespeche.spring_batch_poc.domain.customers.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCustomerDTO(

        @Schema(
                description = "Customer name",
                example = "Software Factory 123"
        )
        @Size(min = 4, max = 20, message = "Customer name must have between 4 and 20 characters")
        @NotBlank(message = "Name is required")
        String name,

        @Schema(
                description = "Customer email",
                example = "software_factory_123@gmail.com"
        )
        @Email(message = "Invalid email format")
        @NotBlank(message = "Email is required")
        String email
) {
}
