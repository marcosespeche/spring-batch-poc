package com.marcosespeche.spring_batch_poc.domain.serviceRequestTypes.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateServiceRequestTypeDTO (

        @Schema(
                description = "Name of the service request type to be created",
                example = "Backend development"
        )
        @Size(min = 5, message = "Name must contain at least 5 characters")
        @NotBlank(message = "Name is required")
        String name,

        @Schema(
                description = "Description of the service request type to be created",
                example = "Backend development"
        )
        String description,

        @Schema(
                description = "Hourly fee to be charged to customers, measured in USD",
                example = "20.5"
        )
        @Min(value = 0, message = "The hourly fee must be a positive number")
        @NotNull(message = "Hourly fee is required")
        Double hourlyFee
){
}
