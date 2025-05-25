package com.ne.rra_vehicle_ms.plate_numbers.dtos;

import com.ne.rra_vehicle_ms.plate_numbers.Status;

import java.time.LocalDate;
import java.util.UUID;

public record PlateNumberResponseDto(
        Long plateId,
        String plateNumber,
        LocalDate issuedDate,
        Status status,
        UUID ownerId,
        String ownerFullNames
) {
}
