package com.ne.rra_vehicle_ms.deductions.dtos;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record DeductionRequestDto(
        @NotBlank(message = "Deduction name is required")
        String deductionName,

        @NotNull(message = "Percentage is required")
        @DecimalMin(value = "0.0", inclusive = true, message = "Percentage must be at least 0")
        @DecimalMax(value = "100.0", inclusive = true, message = "Percentage must be at most 100")
        BigDecimal percentage
) {
}