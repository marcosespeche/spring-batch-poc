package com.marcosespeche.spring_batch_poc.domain.serviceRequests.dtos;

import com.marcosespeche.spring_batch_poc.enums.ServiceRequestState;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record ReadServiceRequestDTO (

        @Schema(
                description = "Service Request identifier",
                example = "10"
        )
        Long id,

        @Schema(
                description = "Service Request description",
                example = "Backend development of billing module"
        )
        String description,

        @Schema(
                description = "Service Request registered date",
                example = "2025-12-27 15:00:15"
        )
        LocalDateTime registeredAt,

        @Schema(
                description = "Service Request finishing date",
                example = "2025-12-27 15:00:15"
        )
        LocalDateTime finishedAt,

        @Schema(
                description = "Service Request state",
                example = "DONE"
        )
        ServiceRequestState state,

        @Schema(
                description = "Project name",
                example = "ERP Maintenance"
        )
        String projectName,

        @Schema(
                description = "Customer name",
                example = "Software Factory 123"
        )
        String customerName,

        @Schema(
                description = "Service Request type",
                example = "Backend development"
        )
        String type
) {
}
