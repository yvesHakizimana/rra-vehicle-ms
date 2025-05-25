package com.ne.rra_vehicle_ms.plate_numbers.service;

import com.ne.rra_vehicle_ms.commons.dtos.PageResponse;
import com.ne.rra_vehicle_ms.plate_numbers.dtos.PlateNumberRequestDto;
import com.ne.rra_vehicle_ms.plate_numbers.dtos.PlateNumberResponseDto;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PlateNumberService {
    /**
     * Register a new plate number under a specific owner
     * 
     * @param requestDto the plate number request data
     * @return the created plate number response
     */
    PlateNumberResponseDto registerPlateNumber(UUID ownerId, PlateNumberRequestDto requestDto);

    /**
     * Get plate numbers associated with a specific owner with pagination
     * 
     * @param ownerId the ID of the owner
     * @param pageable pagination information
     * @return paginated list of plate numbers
     */
    PageResponse<PlateNumberResponseDto> getPlateNumbersByOwner(UUID ownerId, Pageable pageable);
}
