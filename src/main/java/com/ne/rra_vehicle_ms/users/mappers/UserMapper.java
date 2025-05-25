package com.ne.rra_vehicle_ms.users.mappers;


import com.ne.rra_vehicle_ms.auth.dtos.RegisterRequestDto;
import com.ne.rra_vehicle_ms.users.User;
import com.ne.rra_vehicle_ms.users.dtos.UserResponseDto;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface UserMapper {
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    User toEntity(RegisterRequestDto userDto);
    UserResponseDto toResponseDto(User user);
}
