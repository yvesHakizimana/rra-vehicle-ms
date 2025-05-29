package com.ne.rra_vehicle_ms.employment.controllers;

import com.ne.rra_vehicle_ms.employment.dtos.EmploymentRequestDto;
import com.ne.rra_vehicle_ms.employment.dtos.EmploymentResponseDto;
import com.ne.rra_vehicle_ms.employment.entities.EmploymentStatus;
import com.ne.rra_vehicle_ms.employment.services.EmploymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Employment Management", description = "APIs for managing employee employments")
@RestController
@RequestMapping("/api/v1/employments")
@RequiredArgsConstructor
public class EmploymentController {
    private final EmploymentService employmentService;

    @Operation(summary = "Create a new employment", description = "Creates a new employment record for an employee. Requires MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Employment created successfully",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = EmploymentResponseDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input data or employee already has active employment"),
            @ApiResponse(responseCode = "404", description = "Employee not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<EmploymentResponseDto> createEmployment(@Valid @RequestBody EmploymentRequestDto employmentRequestDto) {
        return new ResponseEntity<>(employmentService.createEmployment(employmentRequestDto), HttpStatus.CREATED);
    }

    @Operation(summary = "Get employment by ID", description = "Retrieves an employment record by its UUID. Requires ADMIN or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employment found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = EmploymentResponseDto.class))}),
            @ApiResponse(responseCode = "404", description = "Employment not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<EmploymentResponseDto> getEmploymentById(@PathVariable UUID id) {
        return ResponseEntity.ok(employmentService.getEmploymentById(id));
    }

    @Operation(summary = "Get employment by code", description = "Retrieves an employment record by its code. Requires ADMIN or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employment found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = EmploymentResponseDto.class))}),
            @ApiResponse(responseCode = "404", description = "Employment not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    @GetMapping("/code/{code}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<EmploymentResponseDto> getEmploymentByCode(@PathVariable String code) {
        return ResponseEntity.ok(employmentService.getEmploymentByCode(code));
    }

    @Operation(summary = "Get all employments", description = "Retrieves all employment records. Requires ADMIN or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employments found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = EmploymentResponseDto.class))}),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<EmploymentResponseDto>> getAllEmployments() {
        return ResponseEntity.ok(employmentService.getAllEmployments());
    }

    @Operation(summary = "Get employments by employee", description = "Retrieves all employment records for a specific employee. Requires ADMIN or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employments found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = EmploymentResponseDto.class))}),
            @ApiResponse(responseCode = "404", description = "Employee not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<EmploymentResponseDto>> getEmploymentsByEmployee(@PathVariable UUID employeeId) {
        return ResponseEntity.ok(employmentService.getEmploymentsByEmployee(employeeId));
    }

    @Operation(summary = "Get employments by status", description = "Retrieves all employment records with the specified status. Requires ADMIN or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employments found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = EmploymentResponseDto.class))}),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<EmploymentResponseDto>> getEmploymentsByStatus(@PathVariable EmploymentStatus status) {
        return ResponseEntity.ok(employmentService.getEmploymentsByStatus(status));
    }

    @Operation(summary = "Get current employment", description = "Retrieves the current active employment for a specific employee. Requires ADMIN or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Current employment found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = EmploymentResponseDto.class))}),
            @ApiResponse(responseCode = "404", description = "No active employment found for employee"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    @GetMapping("/employee/{employeeId}/current")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<EmploymentResponseDto> getCurrentEmployment(@PathVariable UUID employeeId) {
        return ResponseEntity.ok(employmentService.getCurrentEmployment(employeeId));
    }

    @Operation(summary = "Update employment", description = "Updates an existing employment record. Requires MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employment updated successfully",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = EmploymentResponseDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Employment not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<EmploymentResponseDto> updateEmployment(@PathVariable UUID id, @Valid @RequestBody EmploymentRequestDto employmentRequestDto) {
        return ResponseEntity.ok(employmentService.updateEmployment(id, employmentRequestDto));
    }

    @Operation(summary = "Delete employment", description = "Deletes an employment record. Requires MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Employment deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Employment not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> deleteEmployment(@PathVariable UUID id) {
        employmentService.deleteEmployment(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update employment status", description = "Updates the status of an existing employment record. Requires MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employment status updated successfully",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = EmploymentResponseDto.class))}),
            @ApiResponse(responseCode = "404", description = "Employment not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<EmploymentResponseDto> updateEmploymentStatus(@PathVariable UUID id, @RequestParam EmploymentStatus status) {
        return ResponseEntity.ok(employmentService.updateEmploymentStatus(id, status));
    }
}