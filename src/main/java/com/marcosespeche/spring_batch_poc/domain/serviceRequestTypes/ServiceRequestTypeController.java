package com.marcosespeche.spring_batch_poc.domain.serviceRequestTypes;

import com.marcosespeche.spring_batch_poc.domain.serviceRequestTypes.dtos.CreateServiceRequestTypeDTO;
import com.marcosespeche.spring_batch_poc.domain.serviceRequestTypes.dtos.ReadServiceRequestTypeDTO;
import com.marcosespeche.spring_batch_poc.domain.serviceRequestTypes.dtos.UpdateServiceRequestTypeDTO;
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

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/service-request-types")
@Tag(name = "Service Request Types", description = "Service Request Type management")
public class ServiceRequestTypeController {

    @Autowired
    private ServiceRequestTypeService service;


    @Operation(
            summary = "Get service request types paged",
            description = "Returns a paginated list of service request types filtered by name"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Data fetched successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReadServiceRequestTypeDTO.class)
                    )),
            @ApiResponse(
                    responseCode = "409",
                    description = "Invalid page data",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    @GetMapping(path = "")
    public ResponseEntity<Page<ReadServiceRequestTypeDTO>> getAll(

            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int pageSize,

            @Parameter(description = "Page number", example = "0")
            @RequestParam(defaultValue = "0") int pageNumber,

            @Parameter(description = "Filter name of service request type", example = "development")
            @RequestParam(defaultValue = "") String filter) {

        return ResponseEntity.ok(service.getAllPaged(filter, PageRequest.of(pageNumber, pageSize)));
    }


    @Operation(
            summary = "Get active service request types",
            description = "Returns all service requests types that are active"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Data fetched successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReadServiceRequestTypeDTO.class)
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
    @GetMapping("/active")
    public ResponseEntity<List<ReadServiceRequestTypeDTO>> getAllActive(
            @Parameter(description = "Service Request Type's name to filter", example = "dev")
            @RequestParam(required = true, defaultValue = "") String filter
    ) {
        return ResponseEntity.ok(service.getAllActive(filter));
    }


    @Operation(
            summary = "Create a service request type",
            description = "Creates a new service request type."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Service request type created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReadServiceRequestTypeDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Duplicate Service Request Type's name",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    @PostMapping("")
    public ResponseEntity<ReadServiceRequestTypeDTO> create(@RequestBody @Valid CreateServiceRequestTypeDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }


    @Operation(
            summary = "Update Service Request Type",
            description = "Update a Service Request Type"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReadServiceRequestTypeDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Service Request Type not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Duplicate name",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<ReadServiceRequestTypeDTO> update (
            @PathVariable Long id,
            @Valid @RequestBody UpdateServiceRequestTypeDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }


    @Operation(
            summary = "Delete or restore Service Request Type",
            description = "Soft delete a Service Request Type, if already deleted, then restores it"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Deleted/restored successfully",
                    content = @Content(
                            schema = @Schema(implementation = ReadServiceRequestTypeDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Service Request Type not found",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ReadServiceRequestTypeDTO> deleteOrRestore(@PathVariable Long id) {
        return ResponseEntity.ok(service.deleteOrRestore(id));
    }


}
