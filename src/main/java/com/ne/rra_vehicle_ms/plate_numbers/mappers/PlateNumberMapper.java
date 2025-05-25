package com.ne.rra_vehicle_ms.plate_numbers.mappers;

import com.ne.rra_vehicle_ms.plate_numbers.PlateNumber;
import com.ne.rra_vehicle_ms.plate_numbers.dtos.PlateNumberRequestDto;
import com.ne.rra_vehicle_ms.plate_numbers.dtos.PlateNumberResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PlateNumberMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vehicle", ignore = true)
    PlateNumber fromDto(PlateNumberRequestDto dto);

    @Mapping(target = "plateId", source = "id")
    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "ownerFullNames", expression = "java(plateNumber.getOwner().getFirstName() + ' ' + plateNumber.getOwner().getLastName())")
    PlateNumberResponseDto toDto(PlateNumber plateNumber);
}
