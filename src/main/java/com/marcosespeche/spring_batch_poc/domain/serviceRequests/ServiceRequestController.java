package com.marcosespeche.spring_batch_poc.domain.serviceRequests;

import com.marcosespeche.spring_batch_poc.domain.serviceRequests.dtos.CreateServiceRequestDTO;
import com.marcosespeche.spring_batch_poc.domain.serviceRequests.dtos.ReadServiceRequestDTO;
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
@RequestMapping(path = "/api/v1/service-requests")
@Tag(name = "Service Requests", description = "Service Request management")
public class ServiceRequestController {

    @Autowired
    private ServiceRequestService service;

    @Operation(
            summary = "Find service requests",
            description = "Find service requests filtering by customer or project name"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Data fetched succesfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReadServiceRequestDTO.class)
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
    public ResponseEntity<Page<ReadServiceRequestDTO>> findByCustomerOrProject(
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
        return ResponseEntity.ok(service.findByCustomerAndProject(filter, PageRequest.of(pageNumber, pageSize)));
    }


    @Operation(
            summary = "Create service request",
            description = "Create new service request"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Service Request created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReadServiceRequestDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid Service Request data",
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
                    description = "Agreement not available for creating service requests",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Service Request Type not available",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    @PostMapping("")
    public ResponseEntity<ReadServiceRequestDTO> create(@RequestBody @Valid CreateServiceRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }


    @Operation(
            summary = "Start service request",
            description = "Changes Service Request state to 'IN PROGRESS'"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Service Request updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReadServiceRequestDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Service Request not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Service Request already started",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    @PutMapping(path = "/started/{id}")
    public ResponseEntity<ReadServiceRequestDTO> start(@PathVariable Long id) {
        return ResponseEntity.ok(service.start(id));
    }


    @Operation(
            summary = "Finish a service request",
            description = "Changes Service Request state to 'DONE'"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Service Request updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReadServiceRequestDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Service Request not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Service Request not available to finishing",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    @PutMapping(path = "/finished/{id}")
    public ResponseEntity<ReadServiceRequestDTO> finish(@PathVariable Long id) {
        return ResponseEntity.ok(service.finish(id));
    }


    @Operation(
            summary = "Delete Service Request",
            description = "Deletes a service requests if is not already started"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Service Request deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Service Request not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Service Request already started",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    @DeleteMapping(path = "{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
