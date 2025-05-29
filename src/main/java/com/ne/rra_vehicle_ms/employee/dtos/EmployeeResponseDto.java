package com.ne.rra_vehicle_ms.employee.dtos;

import com.ne.rra_vehicle_ms.employee.entities.EmployeeStatus;
import com.ne.rra_vehicle_ms.employee.entities.Role;

import java.time.LocalDate;
import java.util.UUID;

public record EmployeeResponseDto(
        UUID id,
        String code,
        String firstName,
        String lastName,
        String email,
        Role role,
        String mobile,
        LocalDate dateOfBirth,
        EmployeeStatus status
) {
}