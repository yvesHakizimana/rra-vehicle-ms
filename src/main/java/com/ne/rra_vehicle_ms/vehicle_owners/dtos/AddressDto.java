package com.ne.rra_vehicle_ms.vehicle_owners.dtos;

import jakarta.validation.constraints.NotBlank;

public record AddressDto(
        @NotBlank(message = "Owner district is required.")
        String district,

        @NotBlank(message = "Owner sector is required.")
        String sector
) {
}
