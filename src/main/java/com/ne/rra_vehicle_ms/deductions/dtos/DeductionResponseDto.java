package com.ne.rra_vehicle_ms.deductions.dtos;

import java.math.BigDecimal;
import java.util.UUID;

public record DeductionResponseDto(
        UUID id,
        String code,
        String deductionName,
        BigDecimal percentage
) {
}