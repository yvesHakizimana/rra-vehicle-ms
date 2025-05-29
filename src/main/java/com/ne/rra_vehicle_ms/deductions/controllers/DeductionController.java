package com.ne.rra_vehicle_ms.deductions.controllers;

import com.ne.rra_vehicle_ms.deductions.entities.Deduction;
import com.ne.rra_vehicle_ms.deductions.services.DeductionService;
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
@RequestMapping("/api/v1/deductions")
@RequiredArgsConstructor
public class DeductionController {
    private final DeductionService deductionService;

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Create a new deduction", description = "Creates a new deduction with the specified details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Deduction created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid deduction details provided"),
            @ApiResponse(responseCode = "403", description = "Forbidden - requires MANAGER role")
    })
    public ResponseEntity<Deduction> createDeduction(@Valid @RequestBody Deduction deduction) {
        return new ResponseEntity<>(deductionService.createDeduction(deduction), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get deduction by ID", description = "Retrieves a deduction by its UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deduction found"),
            @ApiResponse(responseCode = "404", description = "Deduction not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden - requires ADMIN or MANAGER role")
    })
    public ResponseEntity<Deduction> getDeductionById(@PathVariable UUID id) {
        return ResponseEntity.ok(deductionService.getDeductionById(id));
    }

    @GetMapping("/code/{code}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get deduction by code", description = "Retrieves a deduction by its unique code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deduction found"),
            @ApiResponse(responseCode = "404", description = "Deduction not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden - requires ADMIN or MANAGER role")
    })
    public ResponseEntity<Deduction> getDeductionByCode(@PathVariable String code) {
        return ResponseEntity.ok(deductionService.getDeductionByCode(code));
    }


    @GetMapping("/name/{name}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get deduction by name", description = "Retrieves a deduction by its name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deduction found"),
            @ApiResponse(responseCode = "404", description = "Deduction not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden - requires ADMIN or MANAGER role")
    })
    public ResponseEntity<Deduction> getDeductionByName(@PathVariable String name) {
        return ResponseEntity.ok(deductionService.getDeductionByName(name));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    @Operation(summary = "Get all deductions", description = "Retrieves list of all deductions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deductions retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden - requires ADMIN, MANAGER or EMPLOYEE role")
    })
    public ResponseEntity<List<Deduction>> getAllDeductions() {
        return ResponseEntity.ok(deductionService.getAllDeductions());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Update deduction", description = "Updates an existing deduction")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deduction updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid deduction details provided"),
            @ApiResponse(responseCode = "404", description = "Deduction not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden - requires MANAGER role")
    })
    public ResponseEntity<Deduction> updateDeduction(@PathVariable UUID id, @Valid @RequestBody Deduction deduction) {
        return ResponseEntity.ok(deductionService.updateDeduction(id, deduction));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Delete deduction", description = "Deletes an existing deduction")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Deduction deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Deduction not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden - requires MANAGER role")
    })
    public ResponseEntity<Void> deleteDeduction(@PathVariable UUID id) {
        deductionService.deleteDeduction(id);
        return ResponseEntity.noContent().build();
    }
}