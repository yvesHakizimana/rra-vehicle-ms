package com.ne.rra_vehicle_ms.employment.mappers;

import com.ne.rra_vehicle_ms.employee.entities.Employee;
import com.ne.rra_vehicle_ms.employee.mappers.EmployeeMapper;
import com.ne.rra_vehicle_ms.employment.dtos.EmploymentRequestDto;
import com.ne.rra_vehicle_ms.employment.dtos.EmploymentResponseDto;
import com.ne.rra_vehicle_ms.employment.entities.Employment;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true), uses = {EmployeeMapper.class})
public interface EmploymentMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "employee", source = "employeeId")
    Employment toEntity(EmploymentRequestDto dto);

    @Mapping(target = "employee", source = "employee")
    EmploymentResponseDto toResponseDto(Employment employment);

    List<EmploymentResponseDto> toResponseDtoList(List<Employment> employments);

    default Employee map(java.util.UUID value) {
        if (value == null) {
            return null;
        }
        Employee employee = new Employee();
        employee.setId(value);
        return employee;
    }
}