package com.ne.rra_vehicle_ms.employment.services;

import com.ne.rra_vehicle_ms.commons.exceptions.BadRequestException;
import com.ne.rra_vehicle_ms.employee.entities.Employee;
import com.ne.rra_vehicle_ms.employee.services.EmployeeService;
import com.ne.rra_vehicle_ms.employment.dtos.EmploymentRequestDto;
import com.ne.rra_vehicle_ms.employment.dtos.EmploymentResponseDto;
import com.ne.rra_vehicle_ms.employment.entities.Employment;
import com.ne.rra_vehicle_ms.employment.entities.EmploymentStatus;
import com.ne.rra_vehicle_ms.employment.mappers.EmploymentMapper;
import com.ne.rra_vehicle_ms.employment.repositories.EmploymentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmploymentService {
    private final EmploymentRepository employmentRepository;
    private final EmployeeService employeeService;
    private final EmploymentMapper employmentMapper;

    @Transactional
    public EmploymentResponseDto createEmployment(EmploymentRequestDto employmentRequestDto) {
        // Verify that the employee exists - get internal entity for validation
        Employee employee = getEmployeeEntityById(employmentRequestDto.employeeId());

        // Check if employee already has an active employment
        List<Employment> activeEmployments = employmentRepository.findAllByEmployeeAndStatusOrderByJoiningDateDesc(employee, EmploymentStatus.ACTIVE);
        if (!activeEmployments.isEmpty()) {
            throw new BadRequestException("Employee already has an active employment");
        }

        Employment employment = employmentMapper.toEntity(employmentRequestDto);
        employment.setEmployee(employee); // Set the employee entity
        
        Employment savedEmployment = employmentRepository.save(employment);
        return employmentMapper.toResponseDto(savedEmployment);
    }

    @Transactional(readOnly = true)
    public EmploymentResponseDto getEmploymentById(UUID id) {
        Employment employment = getEmploymentEntityById(id);
        return employmentMapper.toResponseDto(employment);
    }

    @Transactional(readOnly = true)
    public EmploymentResponseDto getEmploymentByCode(String code) {
        Employment employment = employmentRepository.findByCode(code)
                .orElseThrow(() -> new EntityNotFoundException("Employment with code " + code + " not found"));
        return employmentMapper.toResponseDto(employment);
    }

    @Transactional(readOnly = true)
    public List<EmploymentResponseDto> getAllEmployments() {
        List<Employment> employments = employmentRepository.findAll();
        return employmentMapper.toResponseDtoList(employments);
    }

    @Transactional(readOnly = true)
    public List<EmploymentResponseDto> getEmploymentsByEmployee(UUID employeeId) {
        Employee employee = getEmployeeEntityById(employeeId);
        List<Employment> employments = employmentRepository.findByEmployee(employee);
        return employmentMapper.toResponseDtoList(employments);
    }

    @Transactional(readOnly = true)
    public List<EmploymentResponseDto> getEmploymentsByStatus(EmploymentStatus status) {
        List<Employment> employments = employmentRepository.findByStatus(status);
        return employmentMapper.toResponseDtoList(employments);
    }

    @Transactional(readOnly = true)
    public EmploymentResponseDto getCurrentEmployment(UUID employeeId) {
        Employee employee = getEmployeeEntityById(employeeId);
        List<Employment> activeEmployments = employmentRepository.findAllByEmployeeAndStatusOrderByJoiningDateDesc(employee, EmploymentStatus.ACTIVE);
        if (activeEmployments.isEmpty()) {
            throw new EntityNotFoundException("No active employment found for employee with id " + employeeId);
        }
        Employment currentEmployment = activeEmployments.get(0); // Return the most recent one
        return employmentMapper.toResponseDto(currentEmployment);
    }

    @Transactional
    public EmploymentResponseDto updateEmployment(UUID id, EmploymentRequestDto employmentRequestDto) {
        Employment employment = getEmploymentEntityById(id);
        
        // Verify that the employee exists if employeeId is being changed
        if (!employment.getEmployee().getId().equals(employmentRequestDto.employeeId())) {
            Employee newEmployee = getEmployeeEntityById(employmentRequestDto.employeeId());
            employment.setEmployee(newEmployee);
        }

        // Update fields
        employment.setDepartment(employmentRequestDto.department());
        employment.setPosition(employmentRequestDto.position());
        employment.setBaseSalary(employmentRequestDto.baseSalary());
        employment.setStatus(employmentRequestDto.status());
        employment.setJoiningDate(employmentRequestDto.joiningDate());

        Employment updatedEmployment = employmentRepository.save(employment);
        return employmentMapper.toResponseDto(updatedEmployment);
    }

    @Transactional
    public void deleteEmployment(UUID id) {
        Employment employment = getEmploymentEntityById(id);
        employmentRepository.delete(employment);
    }

    @Transactional
    public EmploymentResponseDto updateEmploymentStatus(UUID id, EmploymentStatus status) {
        Employment employment = getEmploymentEntityById(id);
        employment.setStatus(status);
        Employment updatedEmployment = employmentRepository.save(employment);
        return employmentMapper.toResponseDto(updatedEmployment);
    }

    // Helper methods for internal operations
    private Employment getEmploymentEntityById(UUID id) {
        return employmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employment with id " + id + " not found"));
    }
    
    private Employee getEmployeeEntityById(UUID employeeId) {
        // Since EmployeeService now returns DTOs, we need a way to get the entity
        // You might need to add a method in EmployeeService to get entity by ID
        // For now, we'll use the existing method and handle the conversion
        try {
            // This assumes you have a method to get employee entity by ID
            // You may need to add this method to EmployeeService
            return employeeService.getEmployeeEntityById(employeeId);
        } catch (Exception e) {
            throw new EntityNotFoundException("Employee with id " + employeeId + " not found");
        }
    }
}