package com.ne.rra_vehicle_ms.vehicle_history.dtos;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for vehicle ownership history response
 */
public record VehicleHistoryResponseDto(
        VehicleBasicDto vehicle,
        List<OwnershipPeriodDto> history,
        HistorySummaryDto summary
) {

    public record VehicleBasicDto(
            String chasisNumber,
            String manufacturer,
            String modelName,
            Integer yearOfManufacture,
            String currentPlateNumber
    ) {}

    public record OwnershipPeriodDto(
            OwnerBasicDto owner,
            String owned,
            BigDecimal bought,
            BigDecimal sold,
            String plate
    ) {}

    public record OwnerBasicDto(
            String name,
            String nationalId
    ) {}

    public record HistorySummaryDto(
            Integer totalOwners,
            Integer totalTransfers,
            BigDecimal originalPrice,
            BigDecimal currentValue,
            BigDecimal totalDepreciation
    ) {}
}