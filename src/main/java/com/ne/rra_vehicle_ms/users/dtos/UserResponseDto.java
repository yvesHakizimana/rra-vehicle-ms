package com.ne.rra_vehicle_ms.users.dtos;

import java.util.UUID;

public record UserResponseDto(
        UUID id,
        String firstName,
        String lastName,
        String email
) {
}
