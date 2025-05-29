package com.ne.rra_vehicle_ms.employee.dtos;

import com.ne.rra_vehicle_ms.commons.validation.password.ValidPassword;
import com.ne.rra_vehicle_ms.commons.validation.phone_number.ValidRwandanPhoneNumber;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record EmployeeRequestDto(
        @NotBlank(message = "First name is required")
        @Size(min = 3, max = 30, message = "First name must be between 3 and 30 characters")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(min = 3, max = 30, message = "Last name must be between 3 and 30 characters")
        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,

        @NotBlank(message = "Password is required")
        @ValidPassword
        String password,

        @NotBlank(message = "Mobile number is required")
        @ValidRwandanPhoneNumber
        String mobile,

        @NotNull(message = "Date of birth is required")
        @Past(message = "Date of birth must be in the past")
        LocalDate dateOfBirth
) {
}