package com.ne.rra_vehicle_ms.vehicles.mappers;

import com.ne.rra_vehicle_ms.plate_numbers.PlateNumber;
import com.ne.rra_vehicle_ms.vehicle_owners.VehicleOwner;
import com.ne.rra_vehicle_ms.vehicles.Vehicle;
import com.ne.rra_vehicle_ms.vehicles.dtos.VehicleRequestDto;
import com.ne.rra_vehicle_ms.vehicles.dtos.VehicleResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface VehicleMapper {

    /**
     * Maps a VehicleRequestDto to a Vehicle entity
     * 
     * @param dto the vehicle request data
     * @param ownerId the ID of the owner
     * @param plateNumberId the ID of the plate number
     * @return the mapped Vehicle entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "currentOwner", source = "ownerId", qualifiedByName = "ownerIdToOwner")
    @Mapping(target = "plateNumber", source = "plateNumberId", qualifiedByName = "plateNumberIdToPlateNumber")
    @Mapping(target = "transferHistory", ignore = true)
    Vehicle fromDto(VehicleRequestDto dto, UUID ownerId, Long plateNumberId);

    /**
     * @deprecated Use {@link #fromDto(VehicleRequestDto, UUID, Long)} instead.
     */
    @Deprecated
    default Vehicle fromDto(VehicleRequestDto dto) {
        throw new UnsupportedOperationException("This method is deprecated. Use fromDto(VehicleRequestDto, UUID, Long) instead.");
    }

    @Mapping(target = "ownerId", source = "currentOwner.id")
    @Mapping(target = "ownerFullNames", expression = "java(vehicle.getCurrentOwner().getFirstName() + ' ' + vehicle.getCurrentOwner().getLastName())")
    @Mapping(target = "plateNumber", source = "plateNumber.plateNumber")
    VehicleResponseDto toDto(Vehicle vehicle);

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
}
