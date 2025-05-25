package com.ne.rra_vehicle_ms.vehicle_history.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO for vehicle transfer response
 */
public record VehicleTransferResponseDto(
        UUID id,
        UUID vehicleId,
        String vehicleDetails,
        UUID previousOwnerId,
        String previousOwnerFullNames,
        UUID newOwnerId,
        String newOwnerFullNames,
        Long previousPlateNumberId,
        String previousPlateNumber,
        Long newPlateNumberId,
        String newPlateNumber,
        BigDecimal transferAmount,
        LocalDate transferDate
) {
}