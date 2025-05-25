package com.ne.rra_vehicle_ms.vehicle_owners;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @Column(name = "district", length = 16)
    private String district;

    @Column(name = "sector", length = 16)
    private String sector;
}