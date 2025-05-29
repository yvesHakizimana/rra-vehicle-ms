package com.ne.rra_vehicle_ms.employment.repositories;

import com.ne.rra_vehicle_ms.employee.entities.Employee;
import com.ne.rra_vehicle_ms.employment.entities.Employment;
import com.ne.rra_vehicle_ms.employment.entities.EmploymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmploymentRepository extends JpaRepository<Employment, UUID> {
    Optional<Employment> findByCode(String code);
    List<Employment> findByEmployee(Employee employee);
    List<Employment> findByEmployeeAndStatus(Employee employee, EmploymentStatus status);
    List<Employment> findByStatus(EmploymentStatus status);
    Optional<Employment> findByEmployeeAndStatusOrderByJoiningDateDesc(Employee employee, EmploymentStatus status);
    List<Employment> findAllByEmployeeAndStatusOrderByJoiningDateDesc(Employee employee, EmploymentStatus status);
    boolean existsByCode(String code);
}