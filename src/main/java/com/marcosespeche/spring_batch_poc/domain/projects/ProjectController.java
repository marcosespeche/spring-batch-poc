package com.marcosespeche.spring_batch_poc.domain.projects;

import com.marcosespeche.spring_batch_poc.domain.projects.dtos.CreateProjectDTO;
import com.marcosespeche.spring_batch_poc.domain.projects.dtos.ReadProjectDTO;
import com.marcosespeche.spring_batch_poc.domain.projects.dtos.UpdateProjectDTO;
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
@RequestMapping(path = "/api/v1/projects")
@Tag(name = "Projects", description = "Project Management")
public class ProjectController {

    @Autowired
    private ProjectService service;

    @Operation(
            summary = "Get projects by name and customer",
            description = "Returns paged projects, filtering by customer and project name"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Data fetched successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReadProjectDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Invalid page data",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Customer not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    @GetMapping(path = "/{customerId}")
    public ResponseEntity<Page<ReadProjectDTO>> getAllByCustomer(
            @Parameter(description = "Customer identifier")
            @PathVariable Long customerId,

            @Parameter(description = "Project name to filter")
            @RequestParam(defaultValue = "") String filter,

            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") int pageSize,

            @Parameter(description = "Page number")
            @RequestParam(defaultValue = "0") int pageNumber) {
        return ResponseEntity.ok(service.findByCustomer(filter, PageRequest.of(pageNumber, pageSize), customerId));
    }


    @Operation(
            summary = "Get all active projects by name and customer",
            description = "Returns all active projects filtering by customer and project name"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Data fetched successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReadProjectDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Customer not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    @GetMapping(path = "/active/{customerId}")
    public ResponseEntity<List<ReadProjectDTO>> getAllActiveByCustomer(
            @Parameter(description = "Customer identifier")
            @PathVariable Long customerId,

            @Parameter(description = "Project name to filter")
            @RequestParam(defaultValue = "") String filter) {
        return ResponseEntity.ok(service.findActiveByCustomer(filter, customerId));
    }


    @Operation(
            summary = "Create a project",
            description = "Creates a project"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Project created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReadProjectDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid project data",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Inactive customer",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Duplicated project name for customer",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Customer not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    @PostMapping(path = "")
    public ResponseEntity<ReadProjectDTO> create(@RequestBody @Valid CreateProjectDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }


    @Operation(
            summary = "Update project data",
            description = "Updates project data"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Project updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReadProjectDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid project data",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Duplicated project name for customer",
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
    @PutMapping(path = "/{id}")
    public ResponseEntity<ReadProjectDTO> update(@PathVariable Long id, @RequestBody @Valid UpdateProjectDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @Operation(
            summary = "Delete or restore project",
            description = "Deletes or restores a project"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Project updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReadProjectDTO.class)
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
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<ReadProjectDTO> deleteOrRestore(@PathVariable Long id) {
        return ResponseEntity.ok(service.deleteOrRestore(id));
    }
}
