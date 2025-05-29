package com.ne.rra_vehicle_ms.deductions.mappers;

import com.ne.rra_vehicle_ms.deductions.dtos.DeductionRequestDto;
import com.ne.rra_vehicle_ms.deductions.dtos.DeductionResponseDto;
import com.ne.rra_vehicle_ms.deductions.entities.Deduction;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface DeductionMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)
    Deduction toEntity(DeductionRequestDto dto);

    DeductionResponseDto toResponseDto(Deduction deduction);

    List<DeductionResponseDto> toResponseDtoList(List<Deduction> deductions);
}