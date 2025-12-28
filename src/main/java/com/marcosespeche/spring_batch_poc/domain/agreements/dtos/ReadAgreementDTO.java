package com.marcosespeche.spring_batch_poc.domain.agreements.dtos;

import com.marcosespeche.spring_batch_poc.enums.AgreementState;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.time.YearMonth;

public record ReadAgreementDTO (

        @Schema(
                description = "Agreement identifier",
                example = "10"
        )
        Long id,

        @Schema(
                description = "Agreement starting period",
                example = "March 2025"
        )
        YearMonth startingPeriod,

        @Schema(
                description = "Agreement ending period",
                example = "December 2025"
        )
        YearMonth endingPeriod,

        @Schema(
                description = "Agreement acceptance date",
                example = "2025-12-27 15:00:15"
        )
        LocalDateTime acceptedAt,

        @Schema(
                description = "Agreement state",
                example = "ACTIVE"
        )
        AgreementState state,

        @Schema(
                description = "Customer name",
                example = "Software Factory 123"
        )
        String customerName,

        @Schema(
                description = "Project name",
                example = "ERP Maintenance"
        )
        String projectName
){
}
