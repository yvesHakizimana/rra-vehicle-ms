package com.ne.rra_vehicle_ms.vehicle_history;

import com.ne.rra_vehicle_ms.plate_numbers.PlateNumber;
import com.ne.rra_vehicle_ms.vehicle_owners.VehicleOwner;
import com.ne.rra_vehicle_ms.vehicles.Vehicle;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleTransfer {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private BigDecimal transferAmount;

    @Column(nullable = false)
    private LocalDate transferDate;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "previous_owner_id", nullable = false)
    private VehicleOwner previousOwner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "new_owner_id", nullable = false)
    private VehicleOwner newOwner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "previous_plate_number_id", nullable = false)
    private PlateNumber previousPlateNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "new_plate_number_id", nullable = false)
    private PlateNumber newPlateNumber;

}
