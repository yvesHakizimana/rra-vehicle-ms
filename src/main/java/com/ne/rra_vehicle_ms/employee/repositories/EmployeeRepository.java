package com.ne.rra_vehicle_ms.employee.repositories;

import com.ne.rra_vehicle_ms.employee.entities.Employee;
import com.ne.rra_vehicle_ms.employee.entities.EmployeeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
    Optional<Employee> findByEmail(String email);
    Optional<Employee> findByCode(String code);
    Optional<Employee> findByMobile(String mobile);
    List<Employee> findByStatus(EmployeeStatus status);
    boolean existsByEmail(String email);
    boolean existsByMobile(String mobile);
    boolean existsByCode(String code);
}