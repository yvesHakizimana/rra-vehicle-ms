package com.ne.rra_vehicle_ms.vehicle_history.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * DTO for requesting a vehicle transfer from one owner to another
 */
public record VehicleTransferRequestDto(
        @NotNull(message = "Transfer amount is required")
        @Positive(message = "Transfer amount must be a positive number")
        BigDecimal transferAmount
) {
}
