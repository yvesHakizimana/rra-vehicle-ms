package com.ne.rra_vehicle_ms.employment.controllers;

import com.ne.rra_vehicle_ms.employment.dtos.EmploymentRequestDto;
import com.ne.rra_vehicle_ms.employment.entities.Employment;
import com.ne.rra_vehicle_ms.employment.entities.EmploymentStatus;
import com.ne.rra_vehicle_ms.employment.services.EmploymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/employments")
@RequiredArgsConstructor
public class EmploymentController {
    private final EmploymentService employmentService;

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Create a new employment", description = "Creates a new employment record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Employment created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<Employment> createEmployment(@Valid @RequestBody EmploymentRequestDto employmentRequest) {
        return new ResponseEntity<>(employmentService.createEmployment(employmentRequest), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get employment by ID", description = "Returns employment details for the given ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employment found"),
            @ApiResponse(responseCode = "404", description = "Employment not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<Employment> getEmploymentById(@PathVariable UUID id) {
        return ResponseEntity.ok(employmentService.getEmploymentById(id));
    }

    @GetMapping("/code/{code}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get employment by code", description = "Returns employment details for the given code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employment found"),
            @ApiResponse(responseCode = "404", description = "Employment not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<Employment> getEmploymentByCode(@PathVariable String code) {
        return ResponseEntity.ok(employmentService.getEmploymentByCode(code));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get all employments", description = "Returns a list of all employments")
    @ApiResponse(responseCode = "200", description = "List of employments retrieved successfully")
    public ResponseEntity<List<Employment>> getAllEmployments() {
        return ResponseEntity.ok(employmentService.getAllEmployments());
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    @Operation(summary = "Get employments by employee ID", description = "Returns all employments for a specific employee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employments found"),
            @ApiResponse(responseCode = "404", description = "Employee not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<List<Employment>> getEmploymentsByEmployee(@PathVariable UUID employeeId) {
        return ResponseEntity.ok(employmentService.getEmploymentsByEmployee(employeeId));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get employments by status", description = "Returns all employments with the specified status")
    @ApiResponse(responseCode = "200", description = "Employments found")
    public ResponseEntity<List<Employment>> getEmploymentsByStatus(@PathVariable EmploymentStatus status) {
        return ResponseEntity.ok(employmentService.getEmploymentsByStatus(status));
    }

    @GetMapping("/employee/{employeeId}/current")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    @Operation(summary = "Get current employment for employee", description = "Returns the current active employment for a specific employee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Current employment found"),
            @ApiResponse(responseCode = "404", description = "No active employment found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<Employment> getCurrentEmployment(@PathVariable UUID employeeId) {
        return ResponseEntity.ok(employmentService.getCurrentEmployment(employeeId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Update employment", description = "Updates an existing employment record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employment updated successfully"),
            @ApiResponse(responseCode = "404", description = "Employment not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<Employment> updateEmployment(@PathVariable UUID id, @Valid @RequestBody Employment employment) {
        return ResponseEntity.ok(employmentService.updateEmployment(id, employment));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Delete employment", description = "Deletes an employment record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Employment deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Employment not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<Void> deleteEmployment(@PathVariable UUID id) {
        employmentService.deleteEmployment(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Update employment status", description = "Updates the status of an employment record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status updated successfully"),
            @ApiResponse(responseCode = "404", description = "Employment not found"),
            @ApiResponse(responseCode = "400", description = "Invalid status"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<Employment> updateEmploymentStatus(@PathVariable UUID id, @RequestParam EmploymentStatus status) {
        return ResponseEntity.ok(employmentService.updateEmploymentStatus(id, status));
    }
}