package com.ne.rra_vehicle_ms.payslip.controllers;

import com.ne.rra_vehicle_ms.message.services.MessageService;
import com.ne.rra_vehicle_ms.payslip.dtos.PayslipRequestDto;
import com.ne.rra_vehicle_ms.payslip.dtos.PayslipResponseDto;
import com.ne.rra_vehicle_ms.payslip.entities.Payslip;
import com.ne.rra_vehicle_ms.payslip.entities.PayslipStatus;
import com.ne.rra_vehicle_ms.payslip.services.PayslipService;
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

@RestController
@RequestMapping("/api/v1/payslips")
@RequiredArgsConstructor
@Tag(name = "Payslip Management", description = "APIs for managing employee payslips")
public class PayslipController {
    private final PayslipService payslipService;
    private final MessageService messageService;

    @Operation(summary = "Create a new payslip", description = "Creates a new payslip for an employee. Requires ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Payslip created successfully",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = PayslipResponseDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input data or payslip already exists"),
            @ApiResponse(responseCode = "404", description = "Employee not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PayslipResponseDto> createPayslip(@Valid @RequestBody PayslipRequestDto payslipRequestDto) {
        return new ResponseEntity<>(payslipService.createPayslip(payslipRequestDto), HttpStatus.CREATED);
    }

    @Operation(summary = "Get payslip by ID", description = "Retrieves a payslip by its UUID. Requires ADMIN or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payslip found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = PayslipResponseDto.class))}),
            @ApiResponse(responseCode = "404", description = "Payslip not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<PayslipResponseDto> getPayslipById(@PathVariable UUID id) {
        return ResponseEntity.ok(payslipService.getPayslipById(id));
    }

    @Operation(summary = "Get payslips by employee", description = "Retrieves all payslips for a specific employee. Requires ADMIN or MANAGER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payslips found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = PayslipResponseDto.class))}),
            @ApiResponse(responseCode = "404", description = "Employee not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<PayslipResponseDto>> getPayslipsByEmployee(@PathVariable UUID employeeId) {
        return ResponseEntity.ok(payslipService.getPayslipsByEmployee(employeeId));
    }

    @Operation(summary = "Get payslips by employee and status", description = "Retrieves payslips for a specific employee with given status. Requires ADMIN or MANAGER role.")
    @GetMapping("/employee/{employeeId}/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<PayslipResponseDto>> getPayslipsByEmployeeAndStatus(
            @PathVariable UUID employeeId, @PathVariable PayslipStatus status) {
        return ResponseEntity.ok(payslipService.getPayslipsByEmployeeAndStatus(employeeId, status));
    }

    @Operation(summary = "Get payslips by status", description = "Retrieves all payslips with the specified status. Requires ADMIN or MANAGER role.")
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<PayslipResponseDto>> getPayslipsByStatus(@PathVariable PayslipStatus status) {
        return ResponseEntity.ok(payslipService.getPayslipsByStatus(status));
    }

    @Operation(summary = "Get payslips by month and year", description = "Retrieves all payslips for a specific month and year. Requires ADMIN or MANAGER role.")
    @GetMapping("/month/{month}/year/{year}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<PayslipResponseDto>> getPayslipsByMonthAndYear(
            @PathVariable Integer month, @PathVariable Integer year) {
        return ResponseEntity.ok(payslipService.getPayslipsByMonthAndYear(month, year));
    }

    @Operation(summary = "Get payslips by month, year and status", description = "Retrieves payslips for specific month, year and status. Requires ADMIN or MANAGER role.")
    @GetMapping("/month/{month}/year/{year}/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<PayslipResponseDto>> getPayslipsByMonthAndYearAndStatus(
            @PathVariable Integer month, @PathVariable Integer year, @PathVariable PayslipStatus status) {
        return ResponseEntity.ok(payslipService.getPayslipsByMonthAndYearAndStatus(month, year, status));
    }

    @Operation(summary = "Get employee payslip for specific month and year", description = "Retrieves a specific employee's payslip for given month and year. Requires ADMIN or MANAGER role.")
    @GetMapping("/employee/{employeeId}/month/{month}/year/{year}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<PayslipResponseDto> getPayslipByEmployeeAndMonthAndYear(
            @PathVariable UUID employeeId, @PathVariable Integer month, @PathVariable Integer year) {
        return ResponseEntity.ok(payslipService.getPayslipByEmployeeAndMonthAndYear(employeeId, month, year));
    }

    @Operation(summary = "Update payslip status", description = "Updates the status of a specific payslip. Requires ADMIN role.")
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PayslipResponseDto> updatePayslipStatus(
            @PathVariable UUID id, @RequestParam PayslipStatus status) {
        PayslipResponseDto updatedPayslipDto = payslipService.updatePayslipStatus(id, status);

        if (status == PayslipStatus.PAID) {
            Payslip updatedPayslip = payslipService.getPayslipEntityById(id);
            messageService.createAndSendPayslipApprovalMessages(List.of(updatedPayslip));
        }

        return ResponseEntity.ok(updatedPayslipDto);
    }

    @Operation(summary = "Generate payslips for month", description = "Generates payslips for all active employees for the specified month and year. Requires ADMIN role.")
    @PostMapping("/generate/month/{month}/year/{year}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PayslipResponseDto>> generatePayslipsForMonth(
            @PathVariable Integer month, @PathVariable Integer year) {
        return ResponseEntity.ok(payslipService.generatePayslipsForMonth(month, year));
    }

    @Operation(summary = "Approve payslips for month", description = "Approves all pending payslips for the specified month and year. Requires ADMIN role.")
    @PatchMapping("/approve/month/{month}/year/{year}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PayslipResponseDto>> approvePayslipsForMonth(
            @PathVariable Integer month, @PathVariable Integer year) {
        return ResponseEntity.ok(payslipService.approvePayslipsForMonth(month, year));
    }
}
