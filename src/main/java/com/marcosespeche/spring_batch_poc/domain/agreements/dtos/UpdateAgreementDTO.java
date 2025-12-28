package com.marcosespeche.spring_batch_poc.domain.agreements.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record UpdateAgreementDTO(

        @Schema(
                description = "Agreement starting month",
                example = "1"
        )
        @Min(value = 1, message = "Month can not be lower than 1")
        @Max(value = 12, message = "Month can not be higher than 12")
        int startingMonth,

        @Schema(
                description = "Agreement starting year",
                example = "2025"
        )
        @Min(value = 1, message = "Month can not be lower than 1")
        @Max(value = 12, message = "Month can not be higher than 12")
        int startingYear,

        @Schema(
                description = "Agreement ending month",
                example = "12"
        )
        @Min(value = 1, message = "Month can not be lower than 1")
        @Max(value = 12, message = "Month can not be higher than 12")
        int endingMonth,

        @Schema(
                description = "Agreement ending year",
                example = "2026"
        )
        @Min(value = 1, message = "Month can not be lower than 1")
        @Max(value = 12, message = "Month can not be higher than 12")
        int endingYear,

        @Schema(
                description = "Project identifier for the agreement",
                example = "1"
        )
        Long projectId
) {
}
