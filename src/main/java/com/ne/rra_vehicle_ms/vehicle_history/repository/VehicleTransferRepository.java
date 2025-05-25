package com.ne.rra_vehicle_ms.vehicle_history.repository;

import com.ne.rra_vehicle_ms.vehicle_history.VehicleTransfer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VehicleTransferRepository extends JpaRepository<VehicleTransfer, UUID> {
    
    /**
     * Find all transfers for a specific vehicle with pagination
     * 
     * @param vehicleId the ID of the vehicle
     * @param pageable pagination information
     * @return paginated list of transfers
     */
    Page<VehicleTransfer> findByVehicleId(UUID vehicleId, Pageable pageable);
    
    /**
     * Find all transfers where the specified owner is the previous owner with pagination
     * 
     * @param previousOwnerId the ID of the previous owner
     * @param pageable pagination information
     * @return paginated list of transfers
     */
    Page<VehicleTransfer> findByPreviousOwnerId(UUID previousOwnerId, Pageable pageable);
    
    /**
     * Find all transfers where the specified owner is the new owner with pagination
     * 
     * @param newOwnerId the ID of the new owner
     * @param pageable pagination information
     * @return paginated list of transfers
     */
    Page<VehicleTransfer> findByNewOwnerId(UUID newOwnerId, Pageable pageable);

    // New methods for vehicle History
    /**
     * Find all transfers for a specific vehicle ordered by transfer date (for history timeline)
     */
    List<VehicleTransfer> findByVehicleIdOrderByTransferDateAsc(UUID vehicleId);


    /**
     * Find vehicle by chassis number through transfer history
     */
    @Query("SELECT DISTINCT vt.vehicle FROM VehicleTransfer vt WHERE vt.vehicle.chasisNumber = :chasisNumber")
    List<VehicleTransfer> findByVehicleChasisNumber(@Param("chasisNumber") String chasisNumber);

    /**
     * Find transfers by vehicle chassis number ordered by date
     */
    @Query("SELECT vt FROM VehicleTransfer vt WHERE vt.vehicle.chasisNumber = :chasisNumber ORDER BY vt.transferDate ASC")
    List<VehicleTransfer> findByVehicleChasisNumberOrderByTransferDateAsc(@Param("chasisNumber") String chasisNumber);

    /**
     * Find transfers by current plate number
     */
    @Query("SELECT vt FROM VehicleTransfer vt WHERE vt.newPlateNumber.plateNumber = :plateNumber ORDER BY vt.transferDate ASC")
    List<VehicleTransfer> findByNewPlateNumberOrderByTransferDateAsc(@Param("plateNumber") String plateNumber);

    /**
     * Find transfers involving a specific owner (as buyer or seller)
     */
    @Query("SELECT vt FROM VehicleTransfer vt WHERE vt.previousOwner.id = :ownerId OR vt.newOwner.id = :ownerId ORDER BY vt.transferDate DESC")
    Page<VehicleTransfer> findByOwnerInvolvedOrderByTransferDateDesc(@Param("ownerId") UUID ownerId, Pageable pageable);

}