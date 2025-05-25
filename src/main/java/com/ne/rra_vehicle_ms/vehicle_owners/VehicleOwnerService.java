package com.ne.rra_vehicle_ms.vehicle_owners;

import com.ne.rra_vehicle_ms.commons.dtos.PageResponse;
import com.ne.rra_vehicle_ms.vehicle_owners.dtos.VehicleOwnerRequestDto;
import com.ne.rra_vehicle_ms.vehicle_owners.dtos.VehicleOwnerResponseDto;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface VehicleOwnerService {
    VehicleOwnerResponseDto registerOwner(VehicleOwnerRequestDto dto);
    PageResponse<VehicleOwnerResponseDto> getAllCarOwners(Pageable pageable);
    PageResponse<VehicleOwnerResponseDto> searchOwners(String query, Pageable pageable);
    VehicleOwnerResponseDto getOwnerById(UUID ownerId);
}
