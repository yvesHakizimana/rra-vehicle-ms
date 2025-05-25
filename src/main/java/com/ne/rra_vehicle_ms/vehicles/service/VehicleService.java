package com.ne.rra_vehicle_ms.vehicles.service;

import com.ne.rra_vehicle_ms.commons.dtos.PageResponse;
import com.ne.rra_vehicle_ms.vehicles.dtos.VehicleRequestDto;
import com.ne.rra_vehicle_ms.vehicles.dtos.VehicleResponseDto;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface VehicleService {
    /**
     * Register a new vehicle with owner and plate number
     * 
     * @param ownerId the ID of the owner
     * @param plateNumberId the ID of the plate number
     * @param requestDto the vehicle request data
     * @return the created vehicle response
     */
    VehicleResponseDto registerVehicle(UUID ownerId, Long plateNumberId, VehicleRequestDto requestDto);

    /**
     * @deprecated Use {@link #registerVehicle(UUID, Long, VehicleRequestDto)} instead.
     */
    @Deprecated
    default VehicleResponseDto registerVehicle(VehicleRequestDto requestDto) {
        throw new UnsupportedOperationException("This method is deprecated. Use registerVehicle(UUID, Long, VehicleRequestDto) instead.");
    }

    /**
     * Get a vehicle by its ID
     * 
     * @param id the vehicle ID
     * @return the vehicle response
     */
    VehicleResponseDto getVehicleById(UUID id);

    /**
     * Get all vehicles with pagination
     * 
     * @param pageable pagination information
     * @return paginated list of vehicles
     */
    PageResponse<VehicleResponseDto> getAllVehicles(Pageable pageable);

    /**
     * Get all vehicles for a specific owner with pagination
     * 
     * @param ownerId the ID of the owner
     * @param pageable pagination information
     * @return paginated list of vehicles
     */
    PageResponse<VehicleResponseDto> getVehiclesByOwner(UUID ownerId, Pageable pageable);
}
