package com.ne.rra_vehicle_ms.vehicle_owners.dtos;

import com.ne.rra_vehicle_ms.commons.validation.national_id.ValidRwandaId;
import com.ne.rra_vehicle_ms.commons.validation.phone_number.ValidRwandanPhoneNumber;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VehicleOwnerRequestDto(
        @NotBlank(message = "First name is required.")
        String firstName,

        @NotBlank(message = "Last name is required.")
        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid.")
        String email,

        @NotBlank
        @ValidRwandanPhoneNumber
        String phoneNumber,

        @NotBlank
        @ValidRwandaId
        String nationalId,

        @NotNull(message = "Owner address is required.")
        AddressDto addressDto
) {
}
