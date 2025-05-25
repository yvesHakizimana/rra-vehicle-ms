package com.ne.rra_vehicle_ms.vehicle_owners.mappers;

import com.ne.rra_vehicle_ms.vehicle_owners.VehicleOwner;
import com.ne.rra_vehicle_ms.vehicle_owners.dtos.VehicleOwnerRequestDto;
import com.ne.rra_vehicle_ms.vehicle_owners.dtos.VehicleOwnerResponseDto;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface VehicleOwnerMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "plateNumbers", ignore = true)
    @Mapping(target = "ownedVehicles", ignore = true)
    @Mapping(target = "vehicleAcquisitions", ignore = true)
    @Mapping(target = "vehicleTransfers", ignore = true)
    @Mapping(target = "email", source = "email")
    @Mapping(target = "address.district", source = "addressDto.district")
    @Mapping(target = "address.sector", source = "addressDto.sector")
    VehicleOwner fromRequest(VehicleOwnerRequestDto dto);

    @Mapping(target = "plateNumbers", expression = "java(owner.getPlateNumbers().stream().map(plate -> plate.getPlateNumber()).collect(java.util.stream.Collectors.toSet()))")
    VehicleOwnerResponseDto toResponse(VehicleOwner owner);
}
