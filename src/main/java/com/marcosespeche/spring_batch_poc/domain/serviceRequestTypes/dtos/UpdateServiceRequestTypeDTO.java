package com.marcosespeche.spring_batch_poc.domain.serviceRequestTypes.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateServiceRequestTypeDTO(

        @Schema(
                description = "New name of the Service Request Type",
                example = "Backend development"
        )
        @Size(min = 3, message = "Name must contain at least 3 characters")
        @NotBlank(message = "Name is required")
        String name,

        @Schema(
                description = "New description of the Service Request Type",
                example = "Backend development"
        )
        String description,

        @Schema(
                description = "New hourly fee of the Service Request Type",
                example = "20.5"
        )
        @Min(value = 0, message = "Hourly fee must be a positive number")
        @NotNull(message = "Hourly fee is required")
        Double hourlyFee
) {
}
