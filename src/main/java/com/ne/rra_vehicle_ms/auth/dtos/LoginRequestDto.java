package com.ne.rra_vehicle_ms.auth.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(
        @NotBlank(message = "Field is required")
        @Email(message = "Email must be valid.")
        String email,

        @NotBlank(message = "Password is required")
        String password
) {
}
