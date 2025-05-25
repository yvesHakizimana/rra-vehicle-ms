package com.ne.rra_vehicle_ms.vehicles;

import com.ne.rra_vehicle_ms.commons.dtos.ApiResponse;
import com.ne.rra_vehicle_ms.commons.dtos.PageResponse;
import com.ne.rra_vehicle_ms.vehicles.dtos.VehicleRequestDto;
import com.ne.rra_vehicle_ms.vehicles.dtos.VehicleResponseDto;
import com.ne.rra_vehicle_ms.vehicles.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@RequestMapping("/api/v1/vehicles")
@RequiredArgsConstructor
@Tag(name = "Vehicle Management", description = "API endpoints for vehicle management operations")
public class VehicleController {

    private final VehicleService vehicleService;

    /**
     * Register a new vehicle (admin only)
     * 
     * @param ownerId the ID of the owner
     * @param plateNumberId the ID of the plate number
     * @param requestDto the vehicle request data
     * @return the created vehicle response
     */
    @PostMapping("/owner/{ownerId}/plate-number/{plateNumberId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Register a new vehicle",
        description = "Registers a new vehicle with specified owner and plate number (admin only)",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<com.ne.rra_vehicle_ms.commons.dtos.ApiResponse<VehicleResponseDto>> registerVehicle(
            @Parameter(description = "Owner UUID") @PathVariable("ownerId") UUID ownerId,
            @Parameter(description = "Plate number ID") @PathVariable("plateNumberId") Long plateNumberId,
            @Parameter(description = "Vehicle details", required = true) @Valid @RequestBody VehicleRequestDto requestDto)
    {
        VehicleResponseDto responseDto = vehicleService.registerVehicle(ownerId, plateNumberId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(com.ne.rra_vehicle_ms.commons.dtos.ApiResponse.success("Vehicle registered successfully", responseDto));
    }

    /**
     * Get a vehicle by its ID
     * 
     * @param id the vehicle ID
     * @return the vehicle response
     */
    @GetMapping("/{id}")
    @Operation(
        summary = "Get vehicle by ID",
        description = "Retrieves vehicle details by its unique identifier"
    )
    public ResponseEntity<com.ne.rra_vehicle_ms.commons.dtos.ApiResponse<VehicleResponseDto>> getVehicleById(
            @Parameter(description = "Vehicle UUID") @PathVariable("id") UUID id)
    {
        VehicleResponseDto responseDto = vehicleService.getVehicleById(id);
        return ResponseEntity.ok(com.ne.rra_vehicle_ms.commons.dtos.ApiResponse.success("Vehicle retrieved successfully", responseDto));
    }

    /**
     * Get all vehicles with pagination
     *
     * @return paginated list of vehicles
     */
    @GetMapping
    @Operation(
        summary = "Get all vehicles",
        description = "Retrieves all vehicles with pagination, sorting and filtering options"
    )
    public ResponseEntity<com.ne.rra_vehicle_ms.commons.dtos.ApiResponse<PageResponse<VehicleResponseDto>>> getAllVehicles(
            @Parameter(description = "Page number (zero-based)") @RequestParam(defaultValue = "0") int pageNumber,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "Field to sort by") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction (asc or desc)") @RequestParam(defaultValue = "desc") String sortOrder
    ) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize,
                "asc".equalsIgnoreCase(sortOrder) ? Sort.Direction.ASC : Sort.Direction.DESC,
                sortBy);
        PageResponse<VehicleResponseDto> pageResponse = vehicleService.getAllVehicles(pageable);
        return ResponseEntity.ok(com.ne.rra_vehicle_ms.commons.dtos.ApiResponse.success("Vehicles retrieved successfully", pageResponse));
    }

    /**
     * Get all vehicles for a specific owner with pagination
     * 
     * @param ownerId the ID of the owner
     * @return paginated list of vehicles
     */
    @GetMapping("/owner/{ownerId}")
    @Operation(
        summary = "Get vehicles by owner",
        description = "Retrieves all vehicles owned by a specific owner with pagination options"
    )
    public ResponseEntity<com.ne.rra_vehicle_ms.commons.dtos.ApiResponse<PageResponse<VehicleResponseDto>>> getVehiclesByOwner(
            @Parameter(description = "Owner UUID") @PathVariable("ownerId") UUID ownerId,
            @Parameter(description = "Page number (zero-based)") @RequestParam(defaultValue = "0") int pageNumber,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "Field to sort by") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction (asc or desc)") @RequestParam(defaultValue = "desc") String sortOrder)
    {
        Pageable pageable = PageRequest.of(pageNumber, pageSize,
                "asc".equalsIgnoreCase(sortOrder) ? Sort.Direction.ASC : Sort.Direction.DESC,
                sortBy);
        PageResponse<VehicleResponseDto> pageResponse = vehicleService.getVehiclesByOwner(ownerId, pageable);
        return ResponseEntity.ok(com.ne.rra_vehicle_ms.commons.dtos.ApiResponse.success("Vehicles retrieved successfully", pageResponse));
    }
}
