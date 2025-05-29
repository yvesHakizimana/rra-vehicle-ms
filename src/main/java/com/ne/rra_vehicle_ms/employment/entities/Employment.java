package com.ne.rra_vehicle_ms.employment.entities;

import com.ne.rra_vehicle_ms.employee.entities.Employee;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "employments", indexes = {
        @Index(name = "idx_employment_code_unq", columnList = "code", unique = true),
        @Index(name = "idx_employment_employee_id", columnList = "employee_id")
})
@Entity
@Builder
public class Employment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String code;

    @PrePersist
    public void generateCode() {
        if (this.code == null) {
            this.code = "EMY-" + String.format("%03d", System.nanoTime() % 1000);
        }
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(nullable = false)
    private String department;

    @Column(nullable = false)
    private String position;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal baseSalary;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmploymentStatus status = EmploymentStatus.ACTIVE;

    @Column(nullable = false)
    private LocalDate joiningDate;
}