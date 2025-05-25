package com.ne.rra_vehicle_ms.vehicles.dtos;

import java.math.BigDecimal;
import java.util.UUID;

public record VehicleResponseDto(
        UUID id,
        String chasisNumber,
        String manufacturer,
        Integer yearOfManufacture,
        BigDecimal price,
        String modelName,
        UUID ownerId,
        String ownerFullNames,
        String plateNumber
) {
}
