package com.ne.rra_vehicle_ms.message.mappers;

import com.ne.rra_vehicle_ms.employee.entities.Employee;
import com.ne.rra_vehicle_ms.employee.mappers.EmployeeMapper;
import com.ne.rra_vehicle_ms.message.dtos.MessageRequestDto;
import com.ne.rra_vehicle_ms.message.dtos.MessageResponseDto;
import com.ne.rra_vehicle_ms.message.entities.Message;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true), uses = {EmployeeMapper.class})
public interface MessageMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employee", source = "employeeId")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    Message toEntity(MessageRequestDto dto);

    @Mapping(target = "employee", source = "employee")
    MessageResponseDto toResponseDto(Message message);

    List<MessageResponseDto> toResponseDtoList(List<Message> messages);

    default Employee map(java.util.UUID value) {
        if (value == null) {
            return null;
        }
        Employee employee = new Employee();
        employee.setId(value);
        return employee;
    }
}