package com.ne.rra_vehicle_ms.payslip.controllers;

import com.ne.rra_vehicle_ms.message.services.MessageService;
import com.ne.rra_vehicle_ms.payslip.entities.Payslip;
import com.ne.rra_vehicle_ms.payslip.entities.PayslipStatus;
import com.ne.rra_vehicle_ms.payslip.services.PayslipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Payslip Controller", description = "APIs for managing employee payslips")
public class PayslipController {
    private final PayslipService payslipService;
    private final MessageService messageService;

    @Operation(summary = "Get payslip by ID", description = "Retrieves a specific payslip by its ID. Role: ADMIN or MANAGER")
    @GetMapping("/payslips/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Payslip> getPayslipById(@PathVariable UUID id) {
        return ResponseEntity.ok(payslipService.getPayslipById(id));
    }

    @Operation(summary = "Get payslips by employee", description = "Retrieves all payslips for a specific employee. Role: ADMIN, MANAGER or EMPLOYEE")
    @GetMapping("/payslips/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<List<Payslip>> getPayslipsByEmployee(@PathVariable UUID employeeId) {
        return ResponseEntity.ok(payslipService.getPayslipsByEmployee(employeeId));
    }

    @Operation(summary = "Get payslips by employee and status", description = "Retrieves payslips for an employee filtered by status. Role: ADMIN, MANAGER or EMPLOYEE")
    @GetMapping("/payslips/employee/{employeeId}/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<List<Payslip>> getPayslipsByEmployeeAndStatus(
            @PathVariable UUID employeeId, @PathVariable PayslipStatus status) {
        return ResponseEntity.ok(payslipService.getPayslipsByEmployeeAndStatus(employeeId, status));
    }

    @Operation(summary = "Get payslips by status", description = "Retrieves all payslips with a specific status. Role: ADMIN or MANAGER")
    @GetMapping("/payslips/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<Payslip>> getPayslipsByStatus(@PathVariable PayslipStatus status) {
        return ResponseEntity.ok(payslipService.getPayslipsByStatus(status));
    }

    @Operation(summary = "Get payslips by month and year", description = "Retrieves all payslips for a specific month and year. Role: ADMIN or MANAGER")
    @GetMapping("/payslips/month/{month}/year/{year}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<Payslip>> getPayslipsByMonthAndYear(
            @PathVariable Integer month, @PathVariable Integer year) {
        return ResponseEntity.ok(payslipService.getPayslipsByMonthAndYear(month, year));
    }

    @Operation(summary = "Get payslips by month, year and status", description = "Retrieves payslips filtered by month, year and status. Role: ADMIN or MANAGER")
    @GetMapping("/payslips/month/{month}/year/{year}/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<Payslip>> getPayslipsByMonthAndYearAndStatus(
            @PathVariable Integer month, @PathVariable Integer year, @PathVariable PayslipStatus status) {
        return ResponseEntity.ok(payslipService.getPayslipsByMonthAndYearAndStatus(month, year, status));
    }

    @Operation(summary = "Get payslip by employee, month and year", description = "Retrieves a specific payslip for an employee in a given month and year. Role: ADMIN, MANAGER or EMPLOYEE")
    @GetMapping("/payslip/employee/{employeeId}/month/{month}/year/{year}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<Payslip> getPayslipByEmployeeAndMonthAndYear(
            @PathVariable UUID employeeId, @PathVariable Integer month, @PathVariable Integer year) {
        return ResponseEntity.ok(payslipService.getPayslipByEmployeeAndMonthAndYear(employeeId, month, year));
    }

    @Operation(summary = "Generate payslips for month", description = "Generates payslips for all employees for a specific month and year. Role: MANAGER")
    @PostMapping("/payroll/process/month/{month}/year/{year}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<Payslip>> generatePayslipsForMonth(
            @PathVariable Integer month, @PathVariable Integer year) {
        List<Payslip> generatedPayslips = payslipService.generatePayslipsForMonth(month, year);
        return new ResponseEntity<>(generatedPayslips, HttpStatus.CREATED);
    }

    @Operation(summary = "Approve payslips for month", description = "Approves all generated payslips for a specific month and year. Role: ADMIN")
    @PostMapping("/payroll/approve/month/{month}/year/{year}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Payslip>> approvePayslipsForMonth(
            @PathVariable Integer month, @PathVariable Integer year) {
        List<Payslip> approvedPayslips = payslipService.approvePayslipsForMonth(month, year);

        messageService.createAndSendPayslipApprovalMessages(approvedPayslips);

        return ResponseEntity.ok(approvedPayslips);
    }

    @Operation(summary = "Update payslip status", description = "Updates the status of a specific payslip. Role: ADMIN")
    @PatchMapping("/payslips/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Payslip> updatePayslipStatus(
            @PathVariable UUID id, @RequestParam PayslipStatus status) {
        Payslip updatedPayslip = payslipService.updatePayslipStatus(id, status);

        if (status == PayslipStatus.PAID) {
            messageService.createAndSendPayslipApprovalMessages(List.of(updatedPayslip));
        }

        return ResponseEntity.ok(updatedPayslip);
    }
}