package com.ne.rra_vehicle_ms.plate_numbers.service.impl;

import com.ne.rra_vehicle_ms.commons.dtos.PageResponse;
import com.ne.rra_vehicle_ms.commons.exceptions.BadRequestException;
import com.ne.rra_vehicle_ms.plate_numbers.PlateNumber;
import com.ne.rra_vehicle_ms.plate_numbers.Status;
import com.ne.rra_vehicle_ms.plate_numbers.dtos.PlateNumberRequestDto;
import com.ne.rra_vehicle_ms.plate_numbers.dtos.PlateNumberResponseDto;
import com.ne.rra_vehicle_ms.plate_numbers.mappers.PlateNumberMapper;
import com.ne.rra_vehicle_ms.plate_numbers.repository.PlateNumberRepository;
import com.ne.rra_vehicle_ms.plate_numbers.service.PlateNumberService;
import com.ne.rra_vehicle_ms.vehicle_owners.VehicleOwner;
import com.ne.rra_vehicle_ms.vehicle_owners.VehicleOwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlateNumberServiceImpl implements PlateNumberService {

    private final PlateNumberRepository plateNumberRepository;
    private final VehicleOwnerRepository vehicleOwnerRepository;
    private final PlateNumberMapper plateNumberMapper;

    @Override
    @Transactional
    public PlateNumberResponseDto registerPlateNumber(UUID ownerId, PlateNumberRequestDto requestDto) {

        // Check if the plate number already exists
        if (plateNumberRepository.existsByPlateNumber(requestDto.plateNumber())) {
            throw new BadRequestException("Plate number " + requestDto.plateNumber() + " already exists");
        }

        // Verify that the owner exists
        VehicleOwner owner = vehicleOwnerRepository.findById(ownerId)
                .orElseThrow(() -> new BadRequestException("Owner with ID " + ownerId + " not found"));

        // Map DTO to entity
        PlateNumber plateNumber = plateNumberMapper.fromDto(requestDto);
        
        // Set the owner explicitly to ensure it's properly set
        plateNumber.setOwner(owner);

        // Set the status to AVAILABLE by default
        plateNumber.setStatus(Status.AVAILABLE);
        
        // Save the plate number
        PlateNumber savedPlateNumber = plateNumberRepository.save(plateNumber);
        
        // Map entity to response DTO
        return plateNumberMapper.toDto(savedPlateNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PlateNumberResponseDto> getPlateNumbersByOwner(UUID ownerId, Pageable pageable) {
        // Verify that the owner exists
        if (!vehicleOwnerRepository.existsById(ownerId)) {
            throw new BadRequestException("Owner with ID " + ownerId + " not found");
        }
        
        // Get paginated plate numbers for the owner
        Page<PlateNumber> plateNumbersPage = plateNumberRepository.findByOwnerId(ownerId, pageable);
        
        // Map entities to DTOs and create a page response
        Page<PlateNumberResponseDto> dtoPage = plateNumbersPage.map(plateNumberMapper::toDto);
        
        return PageResponse.from(dtoPage);
    }
}