package com.ne.rra_vehicle_ms.employee.controllers;

import com.ne.rra_vehicle_ms.employee.dtos.EmployeeRequestDto;
import com.ne.rra_vehicle_ms.employee.dtos.EmployeeResponseDto;
import com.ne.rra_vehicle_ms.employee.entities.EmployeeStatus;
import com.ne.rra_vehicle_ms.employee.services.EmployeeService;
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

@Tag(name = "Employee Management", description = "APIs for managing employees")
@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;

    @Operation(summary = "Create a new employee", description = "Creates a new employee with the provided details. Requires MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Employee created successfully",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = EmployeeResponseDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<EmployeeResponseDto> createEmployee(@Valid @RequestBody EmployeeRequestDto employee) {
        return new ResponseEntity<>(employeeService.createEmployee(employee), HttpStatus.CREATED);
    }

    @Operation(summary = "Get employee by ID", description = "Retrieves an employee by their UUID. Requires ADMIN or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = EmployeeResponseDto.class))}),
            @ApiResponse(responseCode = "404", description = "Employee not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<EmployeeResponseDto> getEmployeeById(@PathVariable UUID id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    @Operation(summary = "Get employee by email", description = "Retrieves an employee by their email address. Requires ADMIN or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = EmployeeResponseDto.class))}),
            @ApiResponse(responseCode = "404", description = "Employee not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    @GetMapping("/email/{email}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<EmployeeResponseDto> getEmployeeByEmail(@PathVariable String email) {
        return ResponseEntity.ok(employeeService.getEmployeeByEmail(email));
    }

    @Operation(summary = "Get employee by code", description = "Retrieves an employee by their employee code. Requires ADMIN or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = EmployeeResponseDto.class))}),
            @ApiResponse(responseCode = "404", description = "Employee not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    @GetMapping("/code/{code}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<EmployeeResponseDto> getEmployeeByCode(@PathVariable String code) {
        return ResponseEntity.ok(employeeService.getEmployeeByCode(code));
    }

    @Operation(summary = "Get all employees", description = "Retrieves all employees. Requires ADMIN or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employees found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = EmployeeResponseDto.class))}),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<EmployeeResponseDto>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @Operation(summary = "Get employees by status", description = "Retrieves all employees with the specified status. Requires ADMIN or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employees found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = EmployeeResponseDto.class))}),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<EmployeeResponseDto>> getEmployeesByStatus(@PathVariable EmployeeStatus status) {
        return ResponseEntity.ok(employeeService.getEmployeesByStatus(status));
    }

    @Operation(summary = "Update employee", description = "Updates an existing employee. Requires MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee updated successfully",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = EmployeeResponseDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Employee not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<EmployeeResponseDto> updateEmployee(@PathVariable UUID id, @Valid @RequestBody EmployeeRequestDto employee) {
        return ResponseEntity.ok(employeeService.updateEmployee(id, employee));
    }

    @Operation(summary = "Delete employee", description = "Deletes an employee. Requires MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Employee deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Employee not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> deleteEmployee(@PathVariable UUID id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update employee status", description = "Updates the status of an existing employee. Requires MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee status updated successfully",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = EmployeeResponseDto.class))}),
            @ApiResponse(responseCode = "404", description = "Employee not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<EmployeeResponseDto> updateEmployeeStatus(@PathVariable UUID id, @RequestParam EmployeeStatus status) {
        return ResponseEntity.ok(employeeService.updateEmployeeStatus(id, status));
    }

    @Operation(summary = "Update employee password", description = "Updates the password of an existing employee. Requires MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee password updated successfully",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = EmployeeResponseDto.class))}),
            @ApiResponse(responseCode = "404", description = "Employee not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    @PatchMapping("/{id}/password")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<EmployeeResponseDto> updateEmployeePassword(@PathVariable UUID id, @RequestParam String newPassword) {
        return ResponseEntity.ok(employeeService.updateEmployeePassword(id, newPassword));
    }
}