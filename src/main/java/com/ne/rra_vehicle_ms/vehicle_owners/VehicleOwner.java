package com.ne.rra_vehicle_ms.vehicle_owners;

import com.ne.rra_vehicle_ms.plate_numbers.PlateNumber;
import com.ne.rra_vehicle_ms.vehicle_history.VehicleTransfer;
import com.ne.rra_vehicle_ms.vehicles.Vehicle;
import jakarta.persistence.*;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "vehicle_owners")
public class VehicleOwner {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String firstName;
    private String lastName;

    @Column(nullable = false, unique = true, length = 10)
    private String phoneNumber;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true, length = 16)
    private String nationalId;

    @Embedded
    private Address address;

    // Relationships.
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<PlateNumber> plateNumbers = new LinkedHashSet<>();

    @OneToMany(mappedBy = "currentOwner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Vehicle> ownedVehicles = new LinkedHashSet<>();

    @OneToMany(mappedBy = "newOwner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<VehicleTransfer> vehicleAcquisitions = new LinkedHashSet<>();

    @OneToMany(mappedBy = "previousOwner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<VehicleTransfer> vehicleTransfers = new LinkedHashSet<>();
}
