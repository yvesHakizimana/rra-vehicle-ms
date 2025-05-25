package com.ne.rra_vehicle_ms.vehicle_owners;

import com.ne.rra_vehicle_ms.commons.dtos.ApiResponse;
import com.ne.rra_vehicle_ms.commons.dtos.PageResponse;
import com.ne.rra_vehicle_ms.vehicle_owners.dtos.VehicleOwnerRequestDto;
import com.ne.rra_vehicle_ms.vehicle_owners.dtos.VehicleOwnerResponseDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.hibernate.validator.constraints.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/vehicle-owners")
@AllArgsConstructor
public class VehicleOwnerController {

    private final VehicleOwnerService vehicleOwnerService;

    @PostMapping
    ResponseEntity<ApiResponse<VehicleOwnerResponseDto>> registerOwner(@Valid @RequestBody VehicleOwnerRequestDto request){
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Vehicle owner registered successfully", vehicleOwnerService.registerOwner(request)));
    }

    @GetMapping
    ResponseEntity<ApiResponse<PageResponse<VehicleOwnerResponseDto>>> getAllCarOwners(
        @RequestParam(defaultValue = "0") int pageNumber,
        @RequestParam(defaultValue = "10") int pageSize,
        @RequestParam(defaultValue = "id") String sortBy,
        @RequestParam(defaultValue = "desc") String sortOrder) {
        
        Pageable pageable = PageRequest.of(pageNumber, pageSize,
            "asc".equalsIgnoreCase(sortOrder) ? Sort.Direction.ASC : Sort.Direction.DESC,
            sortBy);
        
        return ResponseEntity.ok(
            ApiResponse.success("Vehicle owners retrieved successfully", 
                vehicleOwnerService.getAllCarOwners(pageable))
        );
    }

    @GetMapping("/{ownerId}")
    ResponseEntity<ApiResponse<VehicleOwnerResponseDto>> getOwnerById(@PathVariable("ownerId") @UUID String ownerId){
        return ResponseEntity.ok(
                ApiResponse.success("Vehicle owner retrieved successfully", vehicleOwnerService.getOwnerById(java.util.UUID.fromString(ownerId.trim())))
        );
    }

    @GetMapping("/search")
    ResponseEntity<ApiResponse<PageResponse<VehicleOwnerResponseDto>>> searchOwners(
            @RequestParam("searchQuery") String query,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder){

        Pageable pageable = PageRequest.of(pageNumber, pageSize,
                "asc".equalsIgnoreCase(sortOrder) ? Sort.Direction.ASC : Sort.Direction.DESC,
                sortBy);
        return ResponseEntity.ok(
                ApiResponse.success("Vehicle owners retrieved successfully", vehicleOwnerService.searchOwners(query, pageable))
        );
    }




}
