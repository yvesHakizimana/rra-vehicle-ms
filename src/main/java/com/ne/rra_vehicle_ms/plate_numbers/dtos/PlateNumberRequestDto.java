package com.ne.rra_vehicle_ms.plate_numbers.dtos;

import com.ne.rra_vehicle_ms.commons.validation.plate_number.ValidRwandanPlateNumber;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

public record PlateNumberRequestDto(
        @NotBlank(message = "Plate number is required.")
        @ValidRwandanPlateNumber
        String plateNumber,

        @NotNull(message = "Issued date is required.")
        @PastOrPresent(message = "Issued date of the vehicle must be in the past or present now.")
        LocalDate issuedDate
) {
}
