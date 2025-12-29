package com.marcosespeche.spring_batch_poc.domain.agreements;

import com.marcosespeche.spring_batch_poc.domain.agreements.dtos.CreateAgreementDTO;
import com.marcosespeche.spring_batch_poc.domain.agreements.dtos.ReadAgreementDTO;
import com.marcosespeche.spring_batch_poc.domain.agreements.dtos.UpdateAgreementDTO;
import com.marcosespeche.spring_batch_poc.exceptions.ErrorResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/v1/agreements")
@Tag(name = "Agreements", description = "Agreement management")
public class AgreementController {

    @Autowired
    private AgreementService service;

    @Operation(
            summary = "Find agreements",
            description = "Find agreements filtering by customer name or project name"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Data fetched successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReadAgreementDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Invalid page data",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    @GetMapping("")
    public ResponseEntity<Page<ReadAgreementDTO>> findAll(
            @Parameter(description = "Filter")
            @RequestParam(defaultValue = "")
            String filter,

            @Parameter(description = "Page number")
            @RequestParam(defaultValue = "0")
            int pageNumber,

            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10")
            int pageSize
    ) {
        return ResponseEntity.ok(service.findByCustomerOrProject(filter, PageRequest.of(pageNumber, pageSize)));
    }


    @Operation(
            summary = "Create agreement",
            description = "Creates an agreement"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Agreement created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReadAgreementDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Invalid periods",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Project not active",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Customer not active",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Other agreement accepted",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Project not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    @PostMapping("")
    public ResponseEntity<ReadAgreementDTO> create(@RequestBody @Valid CreateAgreementDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }


    @Operation(
            summary = "Update agreement",
            description = "Updates agreement data"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Agreement updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReadAgreementDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Invalid periods",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Agreement already accepted",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "Agreement not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<ReadAgreementDTO> update(
            @PathVariable Long id,
            @RequestBody @Valid UpdateAgreementDTO dto
            ) {
        return ResponseEntity.ok(service.update(id, dto));
    }


    @Operation(
            summary = "Delete agreement",
            description = "Deletes an agreement"
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "Agreement deleted"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Agreement not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),

            @ApiResponse(
                    responseCode = "409",
                    description = "Agreement already accepted",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
    })
    @DeleteMapping(path = "/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }


    @Operation(
            summary = "Accept agreement",
            description = "Accepts an agreement"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Agreement accepted successfully",
                    content = @Content(
                            mediaType = "application/json",
                        schema = @Schema(implementation = ReadAgreementDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Agreement not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Starting period already passed",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Agreement already accepted",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
    })
    @PutMapping(path = "/accepted/{id}")
    public ResponseEntity<ReadAgreementDTO> accept(@PathVariable Long id) {
        return ResponseEntity.ok(service.accept(id));
    }
}
