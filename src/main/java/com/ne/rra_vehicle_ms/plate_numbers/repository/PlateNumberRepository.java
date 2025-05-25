package com.ne.rra_vehicle_ms.plate_numbers.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ne.rra_vehicle_ms.plate_numbers.PlateNumber;
import com.ne.rra_vehicle_ms.vehicle_owners.VehicleOwner;

@Repository
public interface PlateNumberRepository extends JpaRepository<PlateNumber, Long> {
    /**
     * Find all plate numbers associated with a specific owner with pagination
     * 
     * @param owner the owner entity
     * @param pageable pagination information
     * @return paginated list of plate numbers
     */
    Page<PlateNumber> findByOwner(VehicleOwner owner, Pageable pageable);
    
    /**
     * Find all plate numbers associated with a specific owner ID with pagination
     * 
     * @param ownerId the ID of the owner
     * @param pageable pagination information
     * @return paginated list of plate numbers
     */
    Page<PlateNumber> findByOwnerId(UUID ownerId, Pageable pageable);

    boolean existsByPlateNumber(String plateNumber);
}