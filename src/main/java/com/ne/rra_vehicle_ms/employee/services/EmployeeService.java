package com.ne.rra_vehicle_ms.employee.services;

import com.ne.rra_vehicle_ms.employee.dtos.EmployeeRequestDto;
import com.ne.rra_vehicle_ms.employee.entities.Employee;
import com.ne.rra_vehicle_ms.employee.entities.EmployeeStatus;
import com.ne.rra_vehicle_ms.employee.entities.Role;
import com.ne.rra_vehicle_ms.employee.mappers.EmployeeMapper;
import com.ne.rra_vehicle_ms.employee.repositories.EmployeeRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmployeeMapper employeeMapper;

    // seed the admin and manager of the system
    @PostConstruct
    public void seedAdminAndManager() {
        // create admin if not exists
        if (!employeeRepository.existsByEmail("admin@rra.gov.rw")) {
            Employee admin = Employee.builder()
                    .firstName("Admin")
                    .lastName("User")
                    .email("admin@rra.gov.rw")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ROLE_ADMIN)
                    .mobile("0780000000")
                    .dateOfBirth(LocalDate.of(1990, 1, 1))
                    .status(EmployeeStatus.ACTIVE)
                    .build();
            employeeRepository.save(admin);
        }

        // create manager if not exists
        if (!employeeRepository.existsByEmail("manager@rra.gov.rw")) {
            Employee manager = Employee.builder()
                    .firstName("Manager")
                    .lastName("User")
                    .email("manager@rra.gov.rw")
                    .password(passwordEncoder.encode("manager123"))
                    .role(Role.ROLE_MANAGER)
                    .mobile("0780000001")
                    .dateOfBirth(LocalDate.of(1990, 1, 1))
                    .status(EmployeeStatus.ACTIVE)
                    .build();
            employeeRepository.save(manager);
        }
    }


    @Transactional
    public Employee createEmployee(EmployeeRequestDto employeeDto) {
        // Check if employee with same email or mobile already exists
        if (employeeRepository.existsByEmail(employeeDto.email())) {
            throw new EntityExistsException("Employee with email " + employeeDto.email() + " already exists");
        }
        if (employeeRepository.existsByMobile(employeeDto.mobile())) {
            throw new EntityExistsException("Employee with mobile " + employeeDto.mobile() + " already exists");
        }

        Employee employee = employeeMapper.toEntity(employeeDto);
        employee.setPassword(passwordEncoder.encode("emp123"));
        employee.setRole(Role.ROLE_EMPLOYEE);
        return employeeRepository.save(employee);
    }
    @Transactional(readOnly = true)
    public Employee getEmployeeById(UUID id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee with id " + id + " not found"));
    }

    @Transactional(readOnly = true)
    public Employee getEmployeeByEmail(String email) {
        return employeeRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Employee with email " + email + " not found"));
    }

    @Transactional(readOnly = true)
    public Employee getEmployeeByCode(String code) {
        return employeeRepository.findByCode(code)
                .orElseThrow(() -> new EntityNotFoundException("Employee with code " + code + " not found"));
    }

    @Transactional(readOnly = true)
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Employee> getEmployeesByStatus(EmployeeStatus status) {
        return employeeRepository.findByStatus(status);
    }

    @Transactional
    public Employee updateEmployee(UUID id, Employee employeeDetails) {
        Employee employee = getEmployeeById(id);
        
        // Update fields
        employee.setFirstName(employeeDetails.getFirstName());
        employee.setLastName(employeeDetails.getLastName());
        employee.setMobile(employeeDetails.getMobile());
        employee.setDateOfBirth(employeeDetails.getDateOfBirth());
        employee.setStatus(employeeDetails.getStatus());
        
        // Only update email if it's different and not already taken
        if (!employee.getEmail().equals(employeeDetails.getEmail())) {
            if (employeeRepository.existsByEmail(employeeDetails.getEmail())) {
                throw new EntityExistsException("Email " + employeeDetails.getEmail() + " is already in use");
            }
            employee.setEmail(employeeDetails.getEmail());
        }
        
        // Only update mobile if it's different and not already taken
        if (!employee.getMobile().equals(employeeDetails.getMobile())) {
            if (employeeRepository.existsByMobile(employeeDetails.getMobile())) {
                throw new EntityExistsException("Mobile " + employeeDetails.getMobile() + " is already in use");
            }
            employee.setMobile(employeeDetails.getMobile());
        }
        
        return employeeRepository.save(employee);
    }

    @Transactional
    public void deleteEmployee(UUID id) {
        Employee employee = getEmployeeById(id);
        employeeRepository.delete(employee);
    }

    @Transactional
    public Employee updateEmployeeStatus(UUID id, EmployeeStatus status) {
        Employee employee = getEmployeeById(id);
        employee.setStatus(status);
        return employeeRepository.save(employee);
    }

    @Transactional
    public Employee updateEmployeePassword(UUID id, String newPassword) {
        Employee employee = getEmployeeById(id);
        employee.setPassword(passwordEncoder.encode(newPassword));
        return employeeRepository.save(employee);
    }
}