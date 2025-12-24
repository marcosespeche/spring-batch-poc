package com.marcosespeche.spring_batch_poc.exceptions;

import io.swagger.v3.oas.annotations.media.Schema;

public record ErrorResponseDTO(

        @Schema(
                description = "Error message",
                example = "Error message..."
        )
        String error

) {
}
