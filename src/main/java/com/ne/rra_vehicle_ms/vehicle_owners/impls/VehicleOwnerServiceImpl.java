package com.ne.rra_vehicle_ms.vehicle_owners.impls;

import com.ne.rra_vehicle_ms.commons.dtos.PageResponse;
import com.ne.rra_vehicle_ms.commons.exceptions.BadRequestException;
import com.ne.rra_vehicle_ms.vehicle_owners.VehicleOwner;
import com.ne.rra_vehicle_ms.vehicle_owners.VehicleOwnerRepository;
import com.ne.rra_vehicle_ms.vehicle_owners.VehicleOwnerService;
import com.ne.rra_vehicle_ms.vehicle_owners.VehicleOwnerSpecifications;
import com.ne.rra_vehicle_ms.vehicle_owners.dtos.VehicleOwnerRequestDto;
import com.ne.rra_vehicle_ms.vehicle_owners.dtos.VehicleOwnerResponseDto;
import com.ne.rra_vehicle_ms.vehicle_owners.mappers.VehicleOwnerMapper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class VehicleOwnerServiceImpl implements VehicleOwnerService {

    private final VehicleOwnerRepository repository;
    private final VehicleOwnerMapper mapper;

    @Override
    public VehicleOwnerResponseDto registerOwner(VehicleOwnerRequestDto dto) {
        // check whether the owner already exists by email, phone number, or national ID
        if (repository.existsByEmail(dto.email())) {
            throw new BadRequestException("An owner with this email already exists.");
        }
        if (repository.existsByPhoneNumber(dto.phoneNumber())) {
            throw new BadRequestException("An owner with this phone number already exists.");
        }
        if (repository.existsByNationalId(dto.nationalId())) {
            throw new BadRequestException("An owner with this national ID already exists.");
        }
        var vehicleOwner = mapper.fromRequest(dto);
        return mapper.toResponse(repository.save(vehicleOwner));
    }

    @Override
    public PageResponse<VehicleOwnerResponseDto> getAllCarOwners(Pageable pageable) {
        var hasVehicle = VehicleOwnerSpecifications.hasVehiclesSpec();

        var ownersPage = repository.findAll(hasVehicle, pageable);

        return PageResponse.from((ownersPage).map(mapper::toResponse));
    }

    @Override
    public PageResponse<VehicleOwnerResponseDto> searchOwners(String query, Pageable pageable) {
        Specification<VehicleOwner> spec = VehicleOwnerSpecifications.searchOwners(query);

        // this code remains a mystery for us all.
        return PageResponse.from(repository.findAll(spec, pageable).map(mapper::toResponse));
    }

    @Override
    public VehicleOwnerResponseDto getOwnerById(UUID ownerId) {
        var owner =  repository.findById(ownerId).orElseThrow(() -> new BadRequestException("Owner with the id was not found."));
        return mapper.toResponse(owner);
    }
}
