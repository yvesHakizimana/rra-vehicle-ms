package com.ne.rra_vehicle_ms.employee.mappers;

import com.ne.rra_vehicle_ms.employee.dtos.EmployeeRequestDto;
import com.ne.rra_vehicle_ms.employee.dtos.EmployeeResponseDto;
import com.ne.rra_vehicle_ms.employee.entities.Employee;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface EmployeeMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "status", ignore = true)
    Employee toEntity(EmployeeRequestDto dto);

    EmployeeResponseDto toResponseDto(Employee employee);

    List<EmployeeResponseDto> toResponseDtoList(List<Employee> employees);
}