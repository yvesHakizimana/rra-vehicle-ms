package com.ne.rra_vehicle_ms.payslip.mappers;

import com.ne.rra_vehicle_ms.employee.entities.Employee;
import com.ne.rra_vehicle_ms.employee.mappers.EmployeeMapper;
import com.ne.rra_vehicle_ms.payslip.dtos.PayslipRequestDto;
import com.ne.rra_vehicle_ms.payslip.dtos.PayslipResponseDto;
import com.ne.rra_vehicle_ms.payslip.entities.Payslip;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true), uses = {EmployeeMapper.class})
public interface PayslipMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employee", source = "employeeId")
    Payslip toEntity(PayslipRequestDto dto);

    @Mapping(target = "employee", source = "employee")
    PayslipResponseDto toResponseDto(Payslip payslip);

    List<PayslipResponseDto> toResponseDtoList(List<Payslip> payslips);

    default Employee map(java.util.UUID value) {
        if (value == null) {
            return null;
        }
        Employee employee = new Employee();
        employee.setId(value);
        return employee;
    }
}