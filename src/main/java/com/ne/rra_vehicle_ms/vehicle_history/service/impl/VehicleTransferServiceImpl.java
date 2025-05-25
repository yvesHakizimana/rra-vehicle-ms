package com.ne.rra_vehicle_ms.vehicle_history.service.impl;

import com.ne.rra_vehicle_ms.commons.dtos.PageResponse;
import com.ne.rra_vehicle_ms.commons.exceptions.BadRequestException;
import com.ne.rra_vehicle_ms.commons.exceptions.NotFoundException;
import com.ne.rra_vehicle_ms.plate_numbers.PlateNumber;
import com.ne.rra_vehicle_ms.plate_numbers.Status;
import com.ne.rra_vehicle_ms.plate_numbers.repository.PlateNumberRepository;
import com.ne.rra_vehicle_ms.vehicle_history.VehicleTransfer;
import com.ne.rra_vehicle_ms.vehicle_history.dtos.VehicleHistoryResponseDto;
import com.ne.rra_vehicle_ms.vehicle_history.dtos.VehicleTransferRequestDto;
import com.ne.rra_vehicle_ms.vehicle_history.dtos.VehicleTransferResponseDto;
import com.ne.rra_vehicle_ms.vehicle_history.mappers.VehicleTransferMapper;
import com.ne.rra_vehicle_ms.vehicle_history.repository.VehicleTransferRepository;
import com.ne.rra_vehicle_ms.vehicle_history.service.VehicleTransferService;
import com.ne.rra_vehicle_ms.vehicle_owners.VehicleOwner;
import com.ne.rra_vehicle_ms.vehicle_owners.VehicleOwnerRepository;
import com.ne.rra_vehicle_ms.vehicles.Vehicle;
import com.ne.rra_vehicle_ms.vehicles.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VehicleTransferServiceImpl implements VehicleTransferService {

    private final VehicleTransferRepository vehicleTransferRepository;
    private final VehicleRepository vehicleRepository;
    private final VehicleOwnerRepository vehicleOwnerRepository;
    private final PlateNumberRepository plateNumberRepository;
    private final VehicleTransferMapper vehicleTransferMapper;

    @Override
    @Transactional
    public VehicleTransferResponseDto transferVehicle(UUID vehicleId, UUID newOwnerId, Long newPlateNumberId, VehicleTransferRequestDto requestDto) {
        // Validate and get the vehicle
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new BadRequestException("Vehicle with ID " + vehicleId + " not found"));

        // Get the current owner (previous owner after transfer)
        VehicleOwner previousOwner = vehicle.getCurrentOwner();
        if (previousOwner == null) {
            throw new BadRequestException("Vehicle with ID " + vehicleId + " has no current owner");
        }

        // Get the current plate number (previous plate number after transfer)
        PlateNumber previousPlateNumber = vehicle.getPlateNumber();
        if (previousPlateNumber == null) {
            throw new BadRequestException("Vehicle with ID " + vehicleId + " has no plate number");
        }

        // Validate that the plate number is in use
        if (previousPlateNumber.getStatus() != Status.IN_USE) {
            throw new BadRequestException("Plate number " + previousPlateNumber.getPlateNumber() + " is not in use");
        }

        // Validate and get the new owner
        VehicleOwner newOwner = vehicleOwnerRepository.findById(newOwnerId)
                .orElseThrow(() -> new BadRequestException("Owner with ID " + newOwnerId + " not found"));

        // Validate and get the new plate number
        PlateNumber newPlateNumber = plateNumberRepository.findById(newPlateNumberId)
                .orElseThrow(() -> new BadRequestException("Plate number with ID " + newPlateNumberId + " not found"));

        // Validate that the new plate number belongs to the new owner
        if (!newPlateNumber.getOwner().getId().equals(newOwner.getId())) {
            throw new BadRequestException("Plate number with ID " + newPlateNumberId + " does not belong to owner with ID " + newOwnerId);
        }

        // Validate that the new plate number is available
        if (newPlateNumber.getStatus() != Status.AVAILABLE) {
            throw new BadRequestException("Plate number " + newPlateNumber.getPlateNumber() + " is not available");
        }

        // Create the transfer record
        VehicleTransfer transfer = vehicleTransferMapper.fromDto(requestDto, vehicleId, newOwnerId, newPlateNumberId);
        transfer.setPreviousOwner(previousOwner);
        transfer.setPreviousPlateNumber(previousPlateNumber);

        // Update the previous plate number status to AVAILABLE
        previousPlateNumber.setStatus(Status.AVAILABLE);
        previousPlateNumber.setVehicle(null);
        plateNumberRepository.save(previousPlateNumber);

        // Update the new plate number status to IN_USE
        newPlateNumber.setStatus(Status.IN_USE);
        newPlateNumber.setVehicle(vehicle);
        plateNumberRepository.save(newPlateNumber);

        // Update the vehicle's owner and plate number
        vehicle.setCurrentOwner(newOwner);
        vehicle.setPlateNumber(newPlateNumber);
        vehicleRepository.save(vehicle);

        // Save the transfer record
        VehicleTransfer savedTransfer = vehicleTransferRepository.save(transfer);

        // Return the response
        return vehicleTransferMapper.toDto(savedTransfer);
    }


    @Override
    @Transactional(readOnly = true)
    public VehicleTransferResponseDto getTransferById(UUID id) {
        VehicleTransfer transfer = vehicleTransferRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Transfer with ID " + id + " not found"));
        return vehicleTransferMapper.toDto(transfer);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<VehicleTransferResponseDto> getTransfersByVehicle(UUID vehicleId, Pageable pageable) {
        // Verify that the vehicle exists
        if (!vehicleRepository.existsById(vehicleId)) {
            throw new BadRequestException("Vehicle with ID " + vehicleId + " not found");
        }

        // Get paginated transfers for the vehicle
        Page<VehicleTransfer> transfersPage = vehicleTransferRepository.findByVehicleId(vehicleId, pageable);

        // Map entities to DTOs and create a page response
        Page<VehicleTransferResponseDto> dtoPage = transfersPage.map(vehicleTransferMapper::toDto);

        return PageResponse.from(dtoPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<VehicleTransferResponseDto> getAllTransfers(Pageable pageable) {
        // Get paginated transfers
        Page<VehicleTransfer> transfersPage = vehicleTransferRepository.findAll(pageable);

        // Map entities to DTOs and create a page response
        Page<VehicleTransferResponseDto> dtoPage = transfersPage.map(vehicleTransferMapper::toDto);

        return PageResponse.from(dtoPage);
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleHistoryResponseDto getVehicleHistoryByChasisNumber(String chasisNumber) {
        // Find the transfers by chassis number
        List<VehicleTransfer> transfers = vehicleTransferRepository
                .findByVehicleChasisNumberOrderByTransferDateAsc(chasisNumber.trim());

        if (transfers.isEmpty()) {
            throw new NotFoundException("No vehicle found with chassis number: " + chasisNumber);
        }

        Vehicle vehicle = transfers.getFirst().getVehicle();
        return buildVehicleHistory(vehicle, transfers);
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleHistoryResponseDto getVehicleHistoryByPlateNumber(String plateNumber) {
        // Find transfers by current plate number
        List<VehicleTransfer> transfers = vehicleTransferRepository
                .findByNewPlateNumberOrderByTransferDateAsc(plateNumber.trim());

        if (transfers.isEmpty()) {
            throw new NotFoundException("No vehicle found with plate number: " + plateNumber);
        }

        // Get the vehicle from the most recent transfer
        Vehicle vehicle = transfers.get(transfers.size() - 1).getVehicle();

        // Get all transfers for this vehicle
        List<VehicleTransfer> allTransfers = vehicleTransferRepository
                .findByVehicleIdOrderByTransferDateAsc(vehicle.getId());

        return buildVehicleHistory(vehicle, allTransfers);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<VehicleTransferResponseDto> getOwnerTransferHistory(UUID ownerId, Pageable pageable) {
        // Verify owner exists
        if (!vehicleOwnerRepository.existsById(ownerId)) {
            throw new NotFoundException("Owner with ID " + ownerId + " not found");
        }

        Page<VehicleTransfer> transfersPage = vehicleTransferRepository
                .findByOwnerInvolvedOrderByTransferDateDesc(ownerId, pageable);

        Page<VehicleTransferResponseDto> dtoPage = transfersPage.map(vehicleTransferMapper::toDto);

        return PageResponse.from(dtoPage);
    }


    /**
     * Helper method to build vehicle history response
     */
    private VehicleHistoryResponseDto buildVehicleHistory(Vehicle vehicle, List<VehicleTransfer> transfers) {
        // Build vehicle basic info
        VehicleHistoryResponseDto.VehicleBasicDto vehicleBasic =
                new VehicleHistoryResponseDto.VehicleBasicDto(
                        vehicle.getChasisNumber(),
                        vehicle.getManufacturer(),
                        vehicle.getModelName(),
                        vehicle.getYearOfManufacture(),
                        vehicle.getPlateNumber() != null ? vehicle.getPlateNumber().getPlateNumber() : null
                );

        // Build ownership history
        List<VehicleHistoryResponseDto.OwnershipPeriodDto> history = transfers.stream()
                .map(this::buildOwnershipPeriod)
                .toList();

        // Build summary
        VehicleHistoryResponseDto.HistorySummaryDto summary = buildHistorySummary(vehicle, transfers);

        return new VehicleHistoryResponseDto(vehicleBasic, history, summary);
    }


    /**
     * Helper method to build ownership period DTO from a transfer
     */
    private VehicleHistoryResponseDto.OwnershipPeriodDto buildOwnershipPeriod(VehicleTransfer transfer) {
        VehicleOwner newOwner = transfer.getNewOwner();

        VehicleHistoryResponseDto.OwnerBasicDto owner =
                new VehicleHistoryResponseDto.OwnerBasicDto(
                        newOwner.getFirstName() + " " + newOwner.getLastName(),
                        newOwner.getNationalId()
                );

        // Format ownership period
        String owned = formatOwnershipPeriod(transfer);

        return new VehicleHistoryResponseDto.OwnershipPeriodDto(
                owner,
                owned,
                transfer.getTransferAmount(), // bought amount
                null, // sold amount (would need to find next transfer)
                transfer.getNewPlateNumber() != null ? transfer.getNewPlateNumber().getPlateNumber() : null
        );
    }

    /**
     * Helper method to format ownership period
     */
    private String formatOwnershipPeriod(VehicleTransfer transfer) {
        LocalDate startDate = transfer.getTransferDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy");

        // For current owner, show "Present"
        // For past owners, would need to find the next transfer
        return startDate.format(formatter) + " - Present";
    }

    /**
     * Helper method to build history summary
     */
    private VehicleHistoryResponseDto.HistorySummaryDto buildHistorySummary(Vehicle vehicle, List<VehicleTransfer> transfers) {
        int totalOwners = transfers.size();
        int totalTransfers = transfers.size();

        BigDecimal originalPrice = vehicle.getPrice();
        BigDecimal currentValue = transfers.isEmpty() ? originalPrice :
                transfers.get(transfers.size() - 1).getTransferAmount();

        BigDecimal totalDepreciation = originalPrice.subtract(currentValue);

        return new VehicleHistoryResponseDto.HistorySummaryDto(
                totalOwners,
                totalTransfers,
                originalPrice,
                currentValue,
                totalDepreciation
        );
    }
}
