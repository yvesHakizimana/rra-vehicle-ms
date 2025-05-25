package com.ne.rra_vehicle_ms.plate_numbers;

import com.ne.rra_vehicle_ms.commons.dtos.ApiResponse;
import com.ne.rra_vehicle_ms.commons.dtos.PageResponse;
import com.ne.rra_vehicle_ms.plate_numbers.dtos.PlateNumberRequestDto;
import com.ne.rra_vehicle_ms.plate_numbers.dtos.PlateNumberResponseDto;
import com.ne.rra_vehicle_ms.plate_numbers.service.PlateNumberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/plate-numbers")
@RequiredArgsConstructor
public class PlateNumberController {

    private final PlateNumberService plateNumberService;

    /**
     * Register a new plate number
     * 
     * @param ownerId the ID of the owner (from path variable)
     * @param requestDto the plate number request data
     * @return the created plate number response
     */
    @PostMapping("/owner/{ownerId}")
    ResponseEntity<ApiResponse<PlateNumberResponseDto>> registerPlateNumber(
            @PathVariable("ownerId") UUID ownerId,
            @Valid @RequestBody PlateNumberRequestDto requestDto) {
        PlateNumberResponseDto responseDto = plateNumberService.registerPlateNumber(
                ownerId,
                requestDto
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Plate number registered successfully", responseDto));
    }

    /**
     * Get all plate numbers for a specific owner
     * 
     * @param ownerId the ID of the owner
     * @return paginated list of plate numbers
     */
    @GetMapping("/owner/{ownerId}")
    ResponseEntity<ApiResponse<PageResponse<PlateNumberResponseDto>>> getPlateNumbersByOwner(
            @PathVariable("ownerId") UUID ownerId,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize,
                "asc".equalsIgnoreCase(sortOrder) ? Sort.Direction.ASC : Sort.Direction.DESC,
                sortBy);
        PageResponse<PlateNumberResponseDto> pageResponse = plateNumberService.getPlateNumbersByOwner(ownerId, pageable);
        return ResponseEntity.ok(ApiResponse.success("Plate numbers retrieved successfully", pageResponse));
    }
}
