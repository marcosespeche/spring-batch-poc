package com.marcosespeche.spring_batch_poc.domain.serviceRequests.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateServiceRequestDTO(

        @Schema(
                description = "Service Request Type identifier",
                example = "10"
        )
        @NotNull(message = "Service Request Type is required")
        Long typeId,

        @Schema(
                description = "Agreement identifier",
                example = "10"
        )
        @NotNull(message = "Agreement is required")
        Long agreementId,

        @Schema(
                description = "Service Request description",
                example = "Backend development of billing module"
        )
        @Size(min = 5, max = 500, message = "Description must contain between 5 and 500 characters")
        String description
) {
}
