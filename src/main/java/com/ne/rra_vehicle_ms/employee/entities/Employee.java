package com.ne.rra_vehicle_ms.employee.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "employees", indexes = {
        @Index(name = "idx_employee_email_unq", columnList = "email", unique = true),
        @Index(name = "idx_employee_mobile_unq", columnList = "mobile", unique = true),
        @Index(name = "idx_employee_code_unq", columnList = "code", unique = true)
})
@Entity
@Builder
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    @PrePersist
    public void generateCode() {
        if (this.code == null) {
            this.code = "EMP-" + String.format("%03d", System.nanoTime() % 1000);
        }
    }

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private com.ne.rra_vehicle_ms.employee.entities.Role role;

    @Column(nullable = false, unique = true)
    private String mobile;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmployeeStatus status = EmployeeStatus.ACTIVE;
}