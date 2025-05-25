package com.ne.rra_vehicle_ms.vehicle_owners;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VehicleOwnerRepository extends JpaRepository<VehicleOwner, UUID>, JpaSpecificationExecutor<VehicleOwner> {
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByNationalId(String nationalId);
}
