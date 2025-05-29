package com.ne.rra_vehicle_ms.payslip.dtos;

import com.ne.rra_vehicle_ms.payslip.entities.PayslipStatus;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

public record PayslipRequestDto(
        @NotNull(message = "Employee ID is required")
        UUID employeeId,

        @NotNull(message = "House amount is required")
        @PositiveOrZero(message = "House amount must be positive or zero")
        BigDecimal houseAmount,

        @NotNull(message = "Transport amount is required")
        @PositiveOrZero(message = "Transport amount must be positive or zero")
        BigDecimal transportAmount,

        @NotNull(message = "Employee taxed amount is required")
        @PositiveOrZero(message = "Employee taxed amount must be positive or zero")
        BigDecimal employeeTaxedAmount,

        @NotNull(message = "Pension amount is required")
        @PositiveOrZero(message = "Pension amount must be positive or zero")
        BigDecimal pensionAmount,

        @NotNull(message = "Medical insurance amount is required")
        @PositiveOrZero(message = "Medical insurance amount must be positive or zero")
        BigDecimal medicalInsuranceAmount,

        @NotNull(message = "Other taxed amount is required")
        @PositiveOrZero(message = "Other taxed amount must be positive or zero")
        BigDecimal otherTaxedAmount,

        @NotNull(message = "Gross salary is required")
        @Positive(message = "Gross salary must be positive")
        BigDecimal grossSalary,

        @NotNull(message = "Net salary is required")
        @Positive(message = "Net salary must be positive")
        BigDecimal netSalary,

        @NotNull(message = "Month is required")
        @Min(value = 1, message = "Month must be between 1 and 12")
        @Max(value = 12, message = "Month must be between 1 and 12")
        Integer month,

        @NotNull(message = "Year is required")
        @Min(value = 2000, message = "Year must be after 2000")
        Integer year,

        @NotNull(message = "Status is required")
        PayslipStatus status
) {
}