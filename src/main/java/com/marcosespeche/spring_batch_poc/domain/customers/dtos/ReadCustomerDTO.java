package com.marcosespeche.spring_batch_poc.domain.customers.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;

import java.time.LocalDateTime;

public record ReadCustomerDTO(

        @Schema(
                description = "Customer identifier",
                example = "10"
        )
        Long id,

        @Schema(
                description = "Customer name",
                example = "Restaurant 123"
        )
        String name,

        @Schema(
                description = "Customer email",
                example = "restaurant123@gmail.com")
        @Email(message = "Invalid email format")
        String email,

        @Schema(
                description = "Customer soft delete date",
                example = "2025-12-25 12:00:00"
        )
        LocalDateTime softDeleteDate

) {
}
