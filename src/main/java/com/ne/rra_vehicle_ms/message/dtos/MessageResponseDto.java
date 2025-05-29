package com.ne.rra_vehicle_ms.message.dtos;

import com.ne.rra_vehicle_ms.employee.dtos.EmployeeResponseDto;

import java.time.LocalDateTime;
import java.util.UUID;

public record MessageResponseDto(
        UUID id,
        EmployeeResponseDto employee,
        String message,
        Integer month,
        Integer year,
        LocalDateTime createdAt,
        boolean sent
) {
}