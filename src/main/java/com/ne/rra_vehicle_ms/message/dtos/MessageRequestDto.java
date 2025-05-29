package com.ne.rra_vehicle_ms.message.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record MessageRequestDto(
        @NotNull(message = "Employee ID is required")
        UUID employeeId,

        @NotBlank(message = "Message is required")
        String message,

        @NotNull(message = "Month is required")
        @Min(value = 1, message = "Month must be between 1 and 12")
        @Max(value = 12, message = "Month must be between 1 and 12")
        Integer month,

        @NotNull(message = "Year is required")
        @Min(value = 2000, message = "Year must be after 2000")
        Integer year,

        @NotNull(message = "Sent status is required")
        Boolean sent
) {
}