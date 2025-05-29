package com.ne.rra_vehicle_ms.payslip.repositories;

import com.ne.rra_vehicle_ms.employee.entities.Employee;
import com.ne.rra_vehicle_ms.payslip.entities.Payslip;
import com.ne.rra_vehicle_ms.payslip.entities.PayslipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PayslipRepository extends JpaRepository<Payslip, UUID> {
    List<Payslip> findByEmployee(Employee employee);
    List<Payslip> findByEmployeeAndStatus(Employee employee, PayslipStatus status);
    List<Payslip> findByStatus(PayslipStatus status);
    List<Payslip> findByMonthAndYear(Integer month, Integer year);
    List<Payslip> findByMonthAndYearAndStatus(Integer month, Integer year, PayslipStatus status);
    Optional<Payslip> findByEmployeeAndMonthAndYear(Employee employee, Integer month, Integer year);
    boolean existsByEmployeeAndMonthAndYear(Employee employee, Integer month, Integer year);
}