package com.ne.rra_vehicle_ms.payslip.entities;

import com.ne.rra_vehicle_ms.employee.entities.Employee;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "payslips", indexes = {
        @Index(name = "idx_payslip_employee_month_year", columnList = "employee_id,month,year", unique = true)
})
@Entity
@Builder
public class Payslip {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal houseAmount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal transportAmount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal employeeTaxedAmount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal pensionAmount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal medicalInsuranceAmount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal otherTaxedAmount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal grossSalary;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal netSalary;

    @Column(nullable = false)
    private Integer month;

    @Column(nullable = false)
    private Integer year;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PayslipStatus status = PayslipStatus.PENDING;
}