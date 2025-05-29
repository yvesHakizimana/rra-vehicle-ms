package com.ne.rra_vehicle_ms.employment.dtos;

import com.ne.rra_vehicle_ms.employee.dtos.EmployeeResponseDto;
import com.ne.rra_vehicle_ms.employment.entities.EmploymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record EmploymentResponseDto(
        UUID id,
        String code,
        EmployeeResponseDto employee,
        String department,
        String position,
        BigDecimal baseSalary,
        EmploymentStatus status,
        LocalDate joiningDate
) {
}