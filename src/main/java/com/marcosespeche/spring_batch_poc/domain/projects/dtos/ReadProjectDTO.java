package com.marcosespeche.spring_batch_poc.domain.projects.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record ReadProjectDTO(

        @Schema(
                description = "Project identifier",
                example = "10"
        )
        Long id,

        @Schema(
                description = "Project name",
                example = "Accounting System Refactoring"
        )
        String name,

        @Schema(
                description = "Project description",
                example = "Accounting System Refactoring, including Backend and Frontend"
        )
        String description,

        @Schema(
                description = "Project soft delete date",
                example = "2025-12-27 12:00:15")
        LocalDateTime softDeleteDate
) {
}
