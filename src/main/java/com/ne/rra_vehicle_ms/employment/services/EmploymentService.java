package com.ne.rra_vehicle_ms.employment.services;

import com.ne.rra_vehicle_ms.commons.exceptions.BadRequestException;
import com.ne.rra_vehicle_ms.employee.entities.Employee;
import com.ne.rra_vehicle_ms.employee.services.EmployeeService;
import com.ne.rra_vehicle_ms.employment.dtos.EmploymentRequestDto;
import com.ne.rra_vehicle_ms.employment.entities.Employment;
import com.ne.rra_vehicle_ms.employment.entities.EmploymentStatus;
import com.ne.rra_vehicle_ms.employment.mappers.EmploymentMapper;
import com.ne.rra_vehicle_ms.employment.repositories.EmploymentRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmploymentService {
    private final EmploymentRepository employmentRepository;
    private final EmployeeService employeeService;
    private final EmploymentMapper employmentMapper;

    @Transactional
    public Employment createEmployment(EmploymentRequestDto employmentRequestDto) {
        // Verify that the employee exists
        Employee employee = employeeService.getEmployeeById(employmentRequestDto.employeeId());
        if (employee == null) {
            throw new EntityNotFoundException("Employee with id " + employmentRequestDto.employeeId() + " not found");
        }

        // Check if employee already has an active employment
        List<Employment> activeEmployments = employmentRepository.findAllByEmployeeAndStatusOrderByJoiningDateDesc(employee, EmploymentStatus.ACTIVE);
        if (!activeEmployments.isEmpty()) {
            throw new BadRequestException("Employee already has an active employment");
        }

        Employment employment = employmentMapper.toEntity(employmentRequestDto);

        return employmentRepository.save(employment);
    }

    @Transactional(readOnly = true)
    public Employment getEmploymentById(UUID id) {
        return employmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employment with id " + id + " not found"));
    }

    @Transactional(readOnly = true)
    public Employment getEmploymentByCode(String code) {
        return employmentRepository.findByCode(code)
                .orElseThrow(() -> new EntityNotFoundException("Employment with code " + code + " not found"));
    }

    @Transactional(readOnly = true)
    public List<Employment> getAllEmployments() {
        return employmentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Employment> getEmploymentsByEmployee(UUID employeeId) {
        Employee employee = employeeService.getEmployeeById(employeeId);
        return employmentRepository.findByEmployee(employee);
    }

    @Transactional(readOnly = true)
    public List<Employment> getEmploymentsByStatus(EmploymentStatus status) {
        return employmentRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public Employment getCurrentEmployment(UUID employeeId) {
        Employee employee = employeeService.getEmployeeById(employeeId);
        List<Employment> activeEmployments = employmentRepository.findAllByEmployeeAndStatusOrderByJoiningDateDesc(employee, EmploymentStatus.ACTIVE);
        if (activeEmployments.isEmpty()) {
            throw new EntityNotFoundException("No active employment found for employee with id " + employeeId);
        }
        return activeEmployments.get(0); // Return the most recent one (due to OrderByJoiningDateDesc)
    }

    @Transactional
    public Employment updateEmployment(UUID id, Employment employmentDetails) {
        Employment employment = getEmploymentById(id);

        // Update fields
        employment.setDepartment(employmentDetails.getDepartment());
        employment.setPosition(employmentDetails.getPosition());
        employment.setBaseSalary(employmentDetails.getBaseSalary());
        employment.setStatus(employmentDetails.getStatus());
        employment.setJoiningDate(employmentDetails.getJoiningDate());

        return employmentRepository.save(employment);
    }

    @Transactional
    public void deleteEmployment(UUID id) {
        Employment employment = getEmploymentById(id);
        employmentRepository.delete(employment);
    }

    @Transactional
    public Employment updateEmploymentStatus(UUID id, EmploymentStatus status) {
        Employment employment = getEmploymentById(id);
        employment.setStatus(status);
        return employmentRepository.save(employment);
    }
}