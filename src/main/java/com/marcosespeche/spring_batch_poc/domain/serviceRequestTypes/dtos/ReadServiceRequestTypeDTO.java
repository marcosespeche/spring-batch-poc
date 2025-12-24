package com.marcosespeche.spring_batch_poc.domain.serviceRequestTypes.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record ReadServiceRequestTypeDTO(

        @Schema(
                description = "Identifier of Service Request Type",
                example = "1"
        )
        Long id,

        @Schema(
                description = "Name of the Service Request Type",
                example = "Backend Development")
        String name,

        @Schema(
                description = "Description of the Service Request Type",
                example = "Backend Development"
        )
        String description,

        @Schema(
                description = "Soft Delete Date. NULL means active",
                example = "2025-12-24 15:19:01"
        )
        LocalDateTime softDeleteDate,

        @Schema(
              description = "Hourly fee of the Service Request Type",
              example = "20.5"
        )
        Double hourlyFee
) {
}
