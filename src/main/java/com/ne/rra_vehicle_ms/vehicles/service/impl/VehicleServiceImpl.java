package com.ne.rra_vehicle_ms.vehicles.service.impl;

import com.ne.rra_vehicle_ms.commons.dtos.PageResponse;
import com.ne.rra_vehicle_ms.commons.exceptions.BadRequestException;
import com.ne.rra_vehicle_ms.plate_numbers.PlateNumber;
import com.ne.rra_vehicle_ms.plate_numbers.Status;
import com.ne.rra_vehicle_ms.plate_numbers.repository.PlateNumberRepository;
import com.ne.rra_vehicle_ms.vehicle_owners.VehicleOwner;
import com.ne.rra_vehicle_ms.vehicle_owners.VehicleOwnerRepository;
import com.ne.rra_vehicle_ms.vehicles.Vehicle;
import com.ne.rra_vehicle_ms.vehicles.service.VehicleService;
import com.ne.rra_vehicle_ms.vehicles.dtos.VehicleRequestDto;
import com.ne.rra_vehicle_ms.vehicles.dtos.VehicleResponseDto;
import com.ne.rra_vehicle_ms.vehicles.mappers.VehicleMapper;
import com.ne.rra_vehicle_ms.vehicles.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleOwnerRepository vehicleOwnerRepository;
    private final PlateNumberRepository plateNumberRepository;
    private final VehicleMapper vehicleMapper;

    @Override
    @Transactional
    public VehicleResponseDto registerVehicle(UUID ownerId, Long plateNumberId, VehicleRequestDto requestDto) {
        // Check if a vehicle with the same chassis number already exists
        if (vehicleRepository.existsByChasisNumber(requestDto.chasisNumber())) {
            throw new BadRequestException("Vehicle with chassis number " + requestDto.chasisNumber() + " already exists");
        }

        // Verify that the owner exists
        VehicleOwner owner = vehicleOwnerRepository.findById(ownerId)
                .orElseThrow(() -> new BadRequestException("Owner with ID " + ownerId + " not found"));

        // Verify that the plate number exists and is available
        PlateNumber plateNumber = plateNumberRepository.findById(plateNumberId)
                .orElseThrow(() -> new BadRequestException("Plate number with ID " + plateNumberId + " not found"));

        // Check if the plate number is already assigned to another vehicle
        if (vehicleRepository.existsByPlateNumberId(plateNumber.getId())) {
            throw new BadRequestException("Plate number " + plateNumber.getPlateNumber() + " is already assigned to another vehicle");
        }

        // Check if the plate number is available
        if (plateNumber.getStatus() != Status.AVAILABLE) {
            throw new BadRequestException("Plate number " + plateNumber.getPlateNumber() + " is not available");
        }

        // Map DTO to entity
        Vehicle vehicle = vehicleMapper.fromDto(requestDto, ownerId, plateNumberId);

        // Set the owner and plate number explicitly to ensure they're properly set
        vehicle.setCurrentOwner(owner);
        vehicle.setPlateNumber(plateNumber);

        // Update plate number status to IN_USE
        plateNumber.setStatus(Status.IN_USE);
        plateNumberRepository.save(plateNumber);

        // Save the vehicle
        Vehicle savedVehicle = vehicleRepository.save(vehicle);

        // Map entity to response DTO
        return vehicleMapper.toDto(savedVehicle);
    }


    @Override
    @Transactional(readOnly = true)
    public VehicleResponseDto getVehicleById(UUID id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Vehicle with ID " + id + " not found"));

        return vehicleMapper.toDto(vehicle);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<VehicleResponseDto> getAllVehicles(Pageable pageable) {
        Page<Vehicle> vehiclesPage = vehicleRepository.findAll(pageable);

        // Map entities to DTOs and create a page response
        Page<VehicleResponseDto> dtoPage = vehiclesPage.map(vehicleMapper::toDto);

        return PageResponse.from(dtoPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<VehicleResponseDto> getVehiclesByOwner(UUID ownerId, Pageable pageable) {
        // Verify that the owner exists
        if (!vehicleOwnerRepository.existsById(ownerId)) {
            throw new BadRequestException("Owner with ID " + ownerId + " not found");
        }

        // Get paginated vehicles for the owner
        Page<Vehicle> vehiclesPage = vehicleRepository.findByCurrentOwnerId(ownerId, pageable);

        // Map entities to DTOs and create a page response
        Page<VehicleResponseDto> dtoPage = vehiclesPage.map(vehicleMapper::toDto);

        return PageResponse.from(dtoPage);
    }
}
