package com.ne.rra_vehicle_ms.vehicle_owners.dtos;

import java.util.Set;
import java.util.UUID;

public record VehicleOwnerResponseDto(
        UUID id,
        String firstName,
        String lastName,
        String phoneNumber,
        String nationalId,
        Set<String> plateNumbers

) {
}
