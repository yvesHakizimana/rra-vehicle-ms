package com.ne.rra_vehicle_ms.deductions.entities;

import com.ne.rra_vehicle_ms.commons.generators.Base36Generator;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "deductions", indexes = {
        @Index(name = "idx_deduction_code_unq", columnList = "code", unique = true),
        @Index(name = "idx_deduction_name_unq", columnList = "deduction_name", unique = true)
})
@Entity
@Builder
public class Deduction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String code;

    @PrePersist
    protected void onCreate() {
        if (this.code == null) {
            String prefix = "DED";
            this.code = prefix + String.format("%03d", System.nanoTime() % 1000);
        }
    }
    @Column(name = "deduction_name", nullable = false, unique = true)
    private String deductionName;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal percentage;
}