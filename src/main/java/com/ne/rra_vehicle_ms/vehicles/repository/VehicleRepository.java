package com.ne.rra_vehicle_ms.vehicles.repository;

import com.ne.rra_vehicle_ms.vehicles.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {
    /**
     * Find a vehicle by its chassis number
     * 
     * @param chasisNumber the chassis number
     * @return an Optional containing the vehicle if found
     */
    Optional<Vehicle> findByChasisNumber(String chasisNumber);
    
    /**
     * Find all vehicles associated with a specific owner with pagination
     * 
     * @param ownerId the ID of the owner
     * @param pageable pagination information
     * @return paginated list of vehicles
     */
    Page<Vehicle> findByCurrentOwnerId(UUID ownerId, Pageable pageable);
    
    /**
     * Find a vehicle by its plate number ID
     * 
     * @param plateNumberId the ID of the plate number
     * @return an Optional containing the vehicle if found
     */
    Optional<Vehicle> findByPlateNumberId(Long plateNumberId);
    
    /**
     * Check if a vehicle exists with the given chassis number
     * 
     * @param chasisNumber the chassis number
     * @return true if a vehicle exists with the given chassis number
     */
    boolean existsByChasisNumber(String chasisNumber);
    
    /**
     * Check if a vehicle exists with the given plate number ID
     * 
     * @param plateNumberId the ID of the plate number
     * @return true if a vehicle exists with the given plate number ID
     */
    boolean existsByPlateNumberId(Long plateNumberId);
}