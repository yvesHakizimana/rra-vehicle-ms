package com.ne.rra_vehicle_ms.employment.dtos;

import com.ne.rra_vehicle_ms.employment.entities.EmploymentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record EmploymentRequestDto(
        @NotNull(message = "Employee ID is required")
        UUID employeeId,

        @NotBlank(message = "Department is required")
        String department,

        @NotBlank(message = "Position is required")
        String position,

        @NotNull(message = "Base salary is required")
        @Positive(message = "Base salary must be positive")
        BigDecimal baseSalary,

        @NotNull(message = "Status is required")
        EmploymentStatus status,

        @NotNull(message = "Joining date is required")
        @PastOrPresent(message = "Joining date must be in the past or present")
        LocalDate joiningDate
) {
}