package com.ne.rra_vehicle_ms.auth.dtos;

import com.ne.rra_vehicle_ms.commons.validation.national_id.ValidRwandaId;
import com.ne.rra_vehicle_ms.commons.validation.password.ValidPassword;
import com.ne.rra_vehicle_ms.commons.validation.phone_number.ValidRwandanPhoneNumber;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.UUID;


public record RegisterRequestDto(
        @NotBlank(message = "First name is required")
        @Size(min = 3, max = 50, message = "First name must be between 2 and 50 characters long")
        String firstName,

        @NotBlank(message = "Last name is required")
        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid.")
        String email,

        @NotBlank(message = "Phone number is required.")
        @ValidRwandanPhoneNumber
        String phoneNumber,

        @NotBlank(message = "National ID is required.")
        @ValidRwandaId
        String nationalId,

        @NotBlank(message = "Password is required")
        @ValidPassword
        String password
) {
}
