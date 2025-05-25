package com.ne.rra_vehicle_ms.vehicle_history.mappers;

import com.ne.rra_vehicle_ms.plate_numbers.PlateNumber;
import com.ne.rra_vehicle_ms.vehicle_history.VehicleTransfer;
import com.ne.rra_vehicle_ms.vehicle_history.dtos.VehicleTransferRequestDto;
import com.ne.rra_vehicle_ms.vehicle_history.dtos.VehicleTransferResponseDto;
import com.ne.rra_vehicle_ms.vehicle_owners.VehicleOwner;
import com.ne.rra_vehicle_ms.vehicles.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface VehicleTransferMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "transferDate", expression = "java(java.time.LocalDate.now())")
    @Mapping(target = "vehicle", source = "vehicleId", qualifiedByName = "vehicleIdToVehicle")
    @Mapping(target = "previousOwner", ignore = true) // Will be set in service
    @Mapping(target = "newOwner", source = "newOwnerId", qualifiedByName = "ownerIdToOwner")
    @Mapping(target = "previousPlateNumber", ignore = true) // Will be set in service
    @Mapping(target = "newPlateNumber", source = "newPlateNumberId", qualifiedByName = "plateNumberIdToPlateNumber")
    VehicleTransfer fromDto(VehicleTransferRequestDto dto, UUID vehicleId, UUID newOwnerId, Long newPlateNumberId);


    @Mapping(target = "vehicleId", source = "vehicle.id")
    @Mapping(target = "vehicleDetails", expression = "java(formatVehicleDetails(transfer.getVehicle()))")
    @Mapping(target = "previousOwnerId", source = "previousOwner.id")
    @Mapping(target = "previousOwnerFullNames", expression = "java(formatOwnerName(transfer.getPreviousOwner()))")
    @Mapping(target = "newOwnerId", source = "newOwner.id")
    @Mapping(target = "newOwnerFullNames", expression = "java(formatOwnerName(transfer.getNewOwner()))")
    @Mapping(target = "previousPlateNumberId", source = "previousPlateNumber.id")
    @Mapping(target = "previousPlateNumber", source = "previousPlateNumber.plateNumber")
    @Mapping(target = "newPlateNumberId", source = "newPlateNumber.id")
    @Mapping(target = "newPlateNumber", source = "newPlateNumber.plateNumber")
    VehicleTransferResponseDto toDto(VehicleTransfer transfer);

    @Named("vehicleIdToVehicle")
    default Vehicle vehicleIdToVehicle(UUID vehicleId) {
        if (vehicleId == null) {
            return null;
        }
        Vehicle vehicle = new Vehicle();
        vehicle.setId(vehicleId);
        return vehicle;
    }

    @Named("ownerIdToOwner")
    default VehicleOwner ownerIdToOwner(UUID ownerId) {
        if (ownerId == null) {
            return null;
        }
        VehicleOwner owner = new VehicleOwner();
        owner.setId(ownerId);
        return owner;
    }

    @Named("plateNumberIdToPlateNumber")
    default PlateNumber plateNumberIdToPlateNumber(Long plateNumberId) {
        if (plateNumberId == null) {
            return null;
        }
        PlateNumber plateNumber = new PlateNumber();
        plateNumber.setId(plateNumberId);
        return plateNumber;
    }

    default String formatVehicleDetails(Vehicle vehicle) {
        if (vehicle == null) {
            return "";
        }
        return vehicle.getManufacturer() + " " + vehicle.getModelName() + " (" + vehicle.getYearOfManufacture() + ")";
    }

    default String formatOwnerName(VehicleOwner owner) {
        if (owner == null) {
            return "";
        }
        return owner.getFirstName() + " " + owner.getLastName();
    }
}
