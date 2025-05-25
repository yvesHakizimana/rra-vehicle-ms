package com.ne.rra_vehicle_ms.vehicle_history;

import com.ne.rra_vehicle_ms.commons.dtos.ApiResponse;
import com.ne.rra_vehicle_ms.commons.dtos.PageResponse;
import com.ne.rra_vehicle_ms.commons.validation.plate_number.ValidRwandanPlateNumber;
import com.ne.rra_vehicle_ms.vehicle_history.dtos.VehicleHistoryResponseDto;
import com.ne.rra_vehicle_ms.vehicle_history.dtos.VehicleTransferRequestDto;
import com.ne.rra_vehicle_ms.vehicle_history.dtos.VehicleTransferResponseDto;
import com.ne.rra_vehicle_ms.vehicle_history.service.VehicleTransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/vehicle-transfers")
@RequiredArgsConstructor
public class VehicleTransferController {

    private final VehicleTransferService vehicleTransferService;

    /**
     * Transfer a vehicle from its current owner to a new owner (admin only)
     * 
     * @param vehicleId the ID of the vehicle to transfer
     * @param newOwnerId the ID of the new owner
     * @param newPlateNumberId the ID of the new plate number
     * @param requestDto the transfer request data containing the transfer amount
     * @return the created transfer response
     */
    @PostMapping("/vehicle/{vehicleId}/owner/{newOwnerId}/plate-number/{newPlateNumberId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<VehicleTransferResponseDto>> transferVehicle(
            @PathVariable("vehicleId") UUID vehicleId,
            @PathVariable("newOwnerId") UUID newOwnerId,
            @PathVariable("newPlateNumberId") Long newPlateNumberId,
            @Valid @RequestBody VehicleTransferRequestDto requestDto) {
        VehicleTransferResponseDto responseDto = vehicleTransferService.transferVehicle(vehicleId, newOwnerId, newPlateNumberId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Vehicle transferred successfully", responseDto));
    }

    /**
     * Get a specific transfer by its ID
     * 
     * @param id the transfer ID
     * @return the transfer response
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VehicleTransferResponseDto>> getTransferById(
            @PathVariable("id") UUID id) {
        VehicleTransferResponseDto responseDto = vehicleTransferService.getTransferById(id);
        return ResponseEntity.ok(ApiResponse.success("Transfer retrieved successfully", responseDto));
    }

    /**
     * Get all transfers for a specific vehicle with pagination
     * 
     * @param vehicleId the ID of the vehicle
     * @return paginated list of transfers
     */
    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<ApiResponse<PageResponse<VehicleTransferResponseDto>>> getTransfersByVehicle(
            @PathVariable("vehicleId") UUID vehicleId,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize,
                "asc".equalsIgnoreCase(sortOrder) ? Sort.Direction.ASC : Sort.Direction.DESC,
                sortBy);
        PageResponse<VehicleTransferResponseDto> pageResponse = vehicleTransferService.getTransfersByVehicle(vehicleId, pageable);
        return ResponseEntity.ok(ApiResponse.success("Transfers retrieved successfully", pageResponse));
    }

    /**
     * Get all transfers with pagination
     *
     * @return paginated list of transfers
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<VehicleTransferResponseDto>>> getAllTransfers(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize,
                "asc".equalsIgnoreCase(sortOrder) ? Sort.Direction.ASC : Sort.Direction.DESC,
                sortBy);
        PageResponse<VehicleTransferResponseDto> pageResponse = vehicleTransferService.getAllTransfers(pageable);
        return ResponseEntity.ok(ApiResponse.success("Transfers retrieved successfully", pageResponse));
    }


    /**
     * Get vehicle ownership history by chassis number (admin only)
     */
    @GetMapping("/history/chassis/{chasisNumber}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<VehicleHistoryResponseDto>> getVehicleHistoryByChasis(
            @PathVariable("chasisNumber") String chasisNumber) {

        VehicleHistoryResponseDto history = vehicleTransferService.getVehicleHistoryByChasisNumber(chasisNumber);

        return ResponseEntity.ok(
                ApiResponse.success("Vehicle history retrieved successfully", history)
        );
    }

    /**
     * Get vehicle ownership history by plate number (admin only)
     */
    @GetMapping("/history/plate/{plateNumber}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<VehicleHistoryResponseDto>> getVehicleHistoryByPlateNumber(
            @PathVariable("plateNumber")    @ValidRwandanPlateNumber String plateNumber) {

        VehicleHistoryResponseDto history = vehicleTransferService.getVehicleHistoryByPlateNumber(plateNumber);

        return ResponseEntity.ok(
                ApiResponse.success("Vehicle history retrieved successfully", history)
        );
    }

    /**
     * Get owner transfer history (as buyer or seller) with pagination (admin only)
     *
     * @param ownerId the ID of the owner
     * @return paginated list of transfers involving the owner
     */
    @GetMapping("/history/owner/{ownerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<VehicleTransferResponseDto>>> getOwnerTransferHistory(
            @PathVariable("ownerId") UUID ownerId,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "transferDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder) {

        Pageable pageable = PageRequest.of(pageNumber, pageSize,
                "asc".equalsIgnoreCase(sortOrder) ? Sort.Direction.ASC : Sort.Direction.DESC,
                sortBy);

        PageResponse<VehicleTransferResponseDto> ownerHistory = vehicleTransferService.getOwnerTransferHistory(ownerId, pageable);

        return ResponseEntity.ok(
                ApiResponse.success("Owner transfer history retrieved successfully", ownerHistory)
        );
    }
}
