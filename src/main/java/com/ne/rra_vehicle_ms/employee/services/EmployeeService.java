package com.ne.rra_vehicle_ms.employee.services;

import com.ne.rra_vehicle_ms.employee.dtos.EmployeeRequestDto;
import com.ne.rra_vehicle_ms.employee.dtos.EmployeeResponseDto;
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
    public EmployeeResponseDto createEmployee(EmployeeRequestDto employeeDto) {
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
        Employee savedEmployee = employeeRepository.save(employee);
        return employeeMapper.toResponseDto(savedEmployee);
    }

    @Transactional(readOnly = true)
    public EmployeeResponseDto getEmployeeById(UUID id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee with id " + id + " not found"));
        return employeeMapper.toResponseDto(employee);
    }

    @Transactional(readOnly = true)
    public EmployeeResponseDto getEmployeeByEmail(String email) {
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Employee with email " + email + " not found"));
        return employeeMapper.toResponseDto(employee);
    }

    @Transactional(readOnly = true)
    public EmployeeResponseDto getEmployeeByCode(String code) {
        Employee employee = employeeRepository.findByCode(code)
                .orElseThrow(() -> new EntityNotFoundException("Employee with code " + code + " not found"));
        return employeeMapper.toResponseDto(employee);
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponseDto> getAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();
        return employeeMapper.toResponseDtoList(employees);
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponseDto> getEmployeesByStatus(EmployeeStatus status) {
        List<Employee> employees = employeeRepository.findByStatus(status);
        return employeeMapper.toResponseDtoList(employees);
    }

    @Transactional
    public EmployeeResponseDto updateEmployee(UUID id, EmployeeRequestDto employeeDto) {
        Employee employee = getEmployeeEntityById(id);
        
        // Update fields
        employee.setFirstName(employeeDto.firstName());
        employee.setLastName(employeeDto.lastName());
        employee.setMobile(employeeDto.mobile());
        employee.setDateOfBirth(employeeDto.dateOfBirth());
        
        // Only update email if it's different and not already taken
        if (!employee.getEmail().equals(employeeDto.email())) {
            if (employeeRepository.existsByEmail(employeeDto.email())) {
                throw new EntityExistsException("Email " + employeeDto.email() + " is already in use");
            }
            employee.setEmail(employeeDto.email());
        }
        
        // Only update mobile if it's different and not already taken
        if (!employee.getMobile().equals(employeeDto.mobile())) {
            if (employeeRepository.existsByMobile(employeeDto.mobile())) {
                throw new EntityExistsException("Mobile " + employeeDto.mobile() + " is already in use");
            }
            employee.setMobile(employeeDto.mobile());
        }
        
        Employee updatedEmployee = employeeRepository.save(employee);
        return employeeMapper.toResponseDto(updatedEmployee);
    }

    @Transactional
    public void deleteEmployee(UUID id) {
        Employee employee = getEmployeeEntityById(id);
        employeeRepository.delete(employee);
    }

    @Transactional
    public EmployeeResponseDto updateEmployeeStatus(UUID id, EmployeeStatus status) {
        Employee employee = getEmployeeEntityById(id);
        employee.setStatus(status);
        Employee updatedEmployee = employeeRepository.save(employee);
        return employeeMapper.toResponseDto(updatedEmployee);
    }

    @Transactional
    public EmployeeResponseDto updateEmployeePassword(UUID id, String newPassword) {
        Employee employee = getEmployeeEntityById(id);
        employee.setPassword(passwordEncoder.encode(newPassword));
        Employee updatedEmployee = employeeRepository.save(employee);
        return employeeMapper.toResponseDto(updatedEmployee);
    }

    // Add this public method to support other services that need the entity
    public Employee getEmployeeEntityById(UUID id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee with id " + id + " not found"));
    }
}