package com.ne.rra_vehicle_ms.vehicles;

import com.ne.rra_vehicle_ms.plate_numbers.PlateNumber;
import com.ne.rra_vehicle_ms.vehicle_history.VehicleTransfer;
import com.ne.rra_vehicle_ms.vehicle_owners.VehicleOwner;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "Vehicle", indexes = {
        @Index(name = "idx_vehicle_chasisnumber", columnList = "chasisNumber")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 17)
    private String chasisNumber;

    @Column(nullable = false)
    private String manufacturer;

    @Column(nullable = false)
    private Integer yearOfManufacture;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private String modelName;

    // Relationships.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_owner_id", nullable = false)
    private VehicleOwner currentOwner;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plate_number_id", nullable = false)
    private PlateNumber plateNumber;

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<VehicleTransfer> transferHistory;
}
