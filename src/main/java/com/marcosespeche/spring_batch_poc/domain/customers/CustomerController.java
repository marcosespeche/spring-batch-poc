package com.marcosespeche.spring_batch_poc.domain.customers;

import com.marcosespeche.spring_batch_poc.domain.customers.dtos.CreateCustomerDTO;
import com.marcosespeche.spring_batch_poc.domain.customers.dtos.ReadCustomerDTO;
import com.marcosespeche.spring_batch_poc.domain.customers.dtos.UpdateCustomerDTO;
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
@RequestMapping(path = "/api/v1/customers")
@Tag(name = "Customers", description = "Customers management")
public class CustomerController {

    @Autowired
    private CustomerService service;

    @Operation(
            summary = "Get all customers paged",
            description = "Returns all customers using pagination, filtering by name"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Data fetched successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReadCustomerDTO.class)
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
    public ResponseEntity<Page<ReadCustomerDTO>> getAllPaged(
            @Parameter(name = "pageNumber")
            @RequestParam(defaultValue = "0") int pageNumber,

            @Parameter(name = "pageSize")
            @RequestParam(defaultValue = "10") int pageSize,

            @Parameter(name = "nameFilter")
            @RequestParam(defaultValue = "") String filter) {

        return ResponseEntity.ok(service.getAllPaged(filter, PageRequest.of(pageNumber, pageSize)));
    }


    @Operation(
            summary = "Get all active customers paged",
            description = "Returns all active customers using pagination, filtering by name"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Data fetched successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReadCustomerDTO.class)
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
    public ResponseEntity<Page<ReadCustomerDTO>> getAllActivePaged(
            @Parameter(name = "pageNumber")
            @RequestParam(defaultValue = "0") int pageNumber,

            @Parameter(name = "pageSize")
            @RequestParam(defaultValue = "10") int pageSize,

            @Parameter(name = "nameFilter")
            @RequestParam(defaultValue = "") String filter) {

        return ResponseEntity.ok(service.getAllActivePaged(filter, PageRequest.of(pageNumber, pageSize)));
    }


    @Operation(
            summary = "Create customer",
            description = "Create a new Customer"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Customer created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReadCustomerDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Customer name already exists",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    @PostMapping("")
    public ResponseEntity<ReadCustomerDTO> create(@RequestBody @Valid CreateCustomerDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }



    @Operation(
            summary = "Update customer",
            description = "Update a Customer"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Customer updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReadCustomerDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Customer name already exists",
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
    @PutMapping("/{id}")
    public ResponseEntity<ReadCustomerDTO> update(@PathVariable Long id, @RequestBody @Valid UpdateCustomerDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }



    @Operation(
            summary = "Delete or restore a customer",
            description = "Delete or restore a Customer"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Customer deleted/restored successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReadCustomerDTO.class)
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
    @DeleteMapping("/{id}")
    public ResponseEntity<ReadCustomerDTO> deleteOrRestore(@PathVariable Long id) {
        return ResponseEntity.ok(service.deleteOrRestore(id));
    }


}
