package com.ne.rra_vehicle_ms.vehicles.dtos;

import com.ne.rra_vehicle_ms.commons.validation.past_year.PastYear;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record VehicleRequestDto(
        @NotBlank(message = "Chasis number is required")
        @Size(min = 17, max = 17, message = "Chasis number must be exactly 17 characters long.")
        String chasisNumber,

        @NotBlank(message = "Manufacturer is required")
        String manufacturer,

        @NotNull(message = "Year of manufacture is required.")
        @Positive(message = "Year of manufacture must be a positive number.")
        @PastYear(message = "Year of manufacture must be in the past.")
        Integer yearOfManufacture,

        @NotNull(message = "Price is required.")
        @Positive(message = "Price must be a positive number.")
        BigDecimal price,

        @NotBlank(message = "Model name is required.")
        String modelName
) {
}
