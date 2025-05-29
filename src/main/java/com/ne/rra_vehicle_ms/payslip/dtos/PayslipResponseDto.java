package com.ne.rra_vehicle_ms.payslip.dtos;

import com.ne.rra_vehicle_ms.employee.dtos.EmployeeResponseDto;
import com.ne.rra_vehicle_ms.payslip.entities.PayslipStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record PayslipResponseDto(
        UUID id,
        EmployeeResponseDto employee,
        BigDecimal houseAmount,
        BigDecimal transportAmount,
        BigDecimal employeeTaxedAmount,
        BigDecimal pensionAmount,
        BigDecimal medicalInsuranceAmount,
        BigDecimal otherTaxedAmount,
        BigDecimal grossSalary,
        BigDecimal netSalary,
        Integer month,
        Integer year,
        PayslipStatus status
) {
}