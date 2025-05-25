package com.ne.rra_vehicle_ms.vehicle_history.service;

import com.ne.rra_vehicle_ms.commons.dtos.PageResponse;
import com.ne.rra_vehicle_ms.vehicle_history.dtos.VehicleHistoryResponseDto;
import com.ne.rra_vehicle_ms.vehicle_history.dtos.VehicleTransferRequestDto;
import com.ne.rra_vehicle_ms.vehicle_history.dtos.VehicleTransferResponseDto;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface VehicleTransferService {

    /**
     * Transfer a vehicle from its current owner to a new owner
     * 
     * @param vehicleId the ID of the vehicle to transfer
     * @param newOwnerId the ID of the new owner
     * @param newPlateNumberId the ID of the new plate number
     * @param requestDto the transfer request data containing the transfer amount
     * @return the created transfer response
     */
    VehicleTransferResponseDto transferVehicle(UUID vehicleId, UUID newOwnerId, Long newPlateNumberId, VehicleTransferRequestDto requestDto);


    /**
     * Get a specific transfer by its ID
     * 
     * @param id the transfer ID
     * @return the transfer response
     */
    VehicleTransferResponseDto getTransferById(UUID id);

    /**
     * Get all transfers for a specific vehicle with pagination
     * 
     * @param vehicleId the ID of the vehicle
     * @param pageable pagination information
     * @return paginated list of transfers
     */
    PageResponse<VehicleTransferResponseDto> getTransfersByVehicle(UUID vehicleId, Pageable pageable);

    /**
     * Get all transfers with pagination
     * 
     * @param pageable pagination information
     * @return paginated list of transfers
     */
    PageResponse<VehicleTransferResponseDto> getAllTransfers(Pageable pageable);


    // New methods for vehicle history

    /**
     * Get vehicle ownership history by chassis number
     */
    VehicleHistoryResponseDto getVehicleHistoryByChasisNumber(String chasisNumber);

    /**
     * Get vehicle ownership history by plate number
     */
    VehicleHistoryResponseDto getVehicleHistoryByPlateNumber(String plateNumber);

    /**
     * Get owner transfer history (as buyer or seller)
     */
    PageResponse<VehicleTransferResponseDto> getOwnerTransferHistory(UUID ownerId, Pageable pageable);
}
