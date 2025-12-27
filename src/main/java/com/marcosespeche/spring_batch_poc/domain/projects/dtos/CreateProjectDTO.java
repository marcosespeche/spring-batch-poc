package com.marcosespeche.spring_batch_poc.domain.projects.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateProjectDTO (

        @Schema(
                description = "Project name",
                example = "E-Commerce maintenance"
        )
        @Size(min = 5, max = 25, message = "Project name must contain between 5 and 25 characters")
        @NotBlank(message = "Project name is required")
        String name,

        @Schema(
                description = "Project description",
                example = "E-commerce maintenance, including..."
        )
        @Size(max = 500, message = "Project description cannot exceed 500 characters")
        String description,

        @Schema(
                description = "Customer identifier",
                example = "10"
        )
        @NotNull(message = "Customer is required")
        Long customerId
) {
}
