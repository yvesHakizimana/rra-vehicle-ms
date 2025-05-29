package com.ne.rra_vehicle_ms.payslip.services;

import com.ne.rra_vehicle_ms.deductions.entities.Deduction;
import com.ne.rra_vehicle_ms.deductions.services.DeductionService;
import com.ne.rra_vehicle_ms.employee.dtos.EmployeeResponseDto;
import com.ne.rra_vehicle_ms.employee.entities.Employee;
import com.ne.rra_vehicle_ms.employee.entities.EmployeeStatus;
import com.ne.rra_vehicle_ms.employee.services.EmployeeService;
import com.ne.rra_vehicle_ms.employment.dtos.EmploymentResponseDto;
import com.ne.rra_vehicle_ms.employment.entities.Employment;
import com.ne.rra_vehicle_ms.employment.entities.EmploymentStatus;
import com.ne.rra_vehicle_ms.employment.services.EmploymentService;
import com.ne.rra_vehicle_ms.payslip.dtos.PayslipRequestDto;
import com.ne.rra_vehicle_ms.payslip.dtos.PayslipResponseDto;
import com.ne.rra_vehicle_ms.payslip.entities.Payslip;
import com.ne.rra_vehicle_ms.payslip.entities.PayslipStatus;
import com.ne.rra_vehicle_ms.payslip.mappers.PayslipMapper;
import com.ne.rra_vehicle_ms.payslip.repositories.PayslipRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PayslipService {
    private final PayslipRepository payslipRepository;
    private final EmployeeService employeeService;
    private final EmploymentService employmentService;
    private final DeductionService deductionService;
    private final PayslipMapper payslipMapper;

    @Transactional
    public PayslipResponseDto createPayslip(PayslipRequestDto payslipDto) {
        // Convert DTO to entity
        Payslip payslip = payslipMapper.toEntity(payslipDto);

        // Get the employee entity
        Employee employee = employeeService.getEmployeeEntityById(payslipDto.employeeId());
        payslip.setEmployee(employee);

        // Check if payslip for the same employee, month, and year already exists
        if (payslipRepository.existsByEmployeeAndMonthAndYear(
                employee, payslipDto.month(), payslipDto.year())) {
            throw new EntityExistsException("Payslip for employee " + employee.getCode() +
                    " for month " + payslipDto.month() + " and year " + payslipDto.year() + " already exists");
        }

        // Save the entity and convert back to DTO
        Payslip savedPayslip = payslipRepository.save(payslip);
        return payslipMapper.toResponseDto(savedPayslip);
    }

    @Transactional(readOnly = true)
    public PayslipResponseDto getPayslipById(UUID id) {
        return payslipMapper.toResponseDto(getPayslipEntityById(id));
    }

    // Public method to get entity - needed by other services
    public Payslip getPayslipEntityById(UUID id) {
        return payslipRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payslip with id " + id + " not found"));
    }

    @Transactional(readOnly = true)
    public List<PayslipResponseDto> getPayslipsByEmployee(UUID employeeId) {
        Employee employee = employeeService.getEmployeeEntityById(employeeId);
        List<Payslip> payslips = payslipRepository.findByEmployee(employee);
        return payslipMapper.toResponseDtoList(payslips);
    }

    @Transactional(readOnly = true)
    public List<PayslipResponseDto> getPayslipsByEmployeeAndStatus(UUID employeeId, PayslipStatus status) {
        Employee employee = employeeService.getEmployeeEntityById(employeeId);
        List<Payslip> payslips = payslipRepository.findByEmployeeAndStatus(employee, status);
        return payslipMapper.toResponseDtoList(payslips);
    }

    @Transactional(readOnly = true)
    public List<PayslipResponseDto> getPayslipsByStatus(PayslipStatus status) {
        List<Payslip> payslips = payslipRepository.findByStatus(status);
        return payslipMapper.toResponseDtoList(payslips);
    }

    @Transactional(readOnly = true)
    public List<PayslipResponseDto> getPayslipsByMonthAndYear(Integer month, Integer year) {
        List<Payslip> payslips = payslipRepository.findByMonthAndYear(month, year);
        return payslipMapper.toResponseDtoList(payslips);
    }

    @Transactional(readOnly = true)
    public List<PayslipResponseDto> getPayslipsByMonthAndYearAndStatus(Integer month, Integer year, PayslipStatus status) {
        List<Payslip> payslips = payslipRepository.findByMonthAndYearAndStatus(month, year, status);
        return payslipMapper.toResponseDtoList(payslips);
    }

    @Transactional(readOnly = true)
    public PayslipResponseDto getPayslipByEmployeeAndMonthAndYear(UUID employeeId, Integer month, Integer year) {
        Employee employee = employeeService.getEmployeeEntityById(employeeId);
        Payslip payslip = payslipRepository.findByEmployeeAndMonthAndYear(employee, month, year)
                .orElseThrow(() -> new EntityNotFoundException("Payslip for employee " + employee.getCode() +
                        " for month " + month + " and year " + year + " not found"));
        return payslipMapper.toResponseDto(payslip);
    }

    @Transactional
    public PayslipResponseDto updatePayslipStatus(UUID id, PayslipStatus status) {
        Payslip payslip = getPayslipEntityById(id);
        payslip.setStatus(status);
        Payslip updatedPayslip = payslipRepository.save(payslip);
        return payslipMapper.toResponseDto(updatedPayslip);
    }

    @Transactional
    public List<PayslipResponseDto> generatePayslipsForMonth(Integer month, Integer year) {
        // Get all active employees using DTOs
        List<EmployeeResponseDto> activeEmployeeDtos = employeeService.getEmployeesByStatus(EmployeeStatus.ACTIVE);
        List<Payslip> generatedPayslips = new ArrayList<>();

        for (EmployeeResponseDto employeeDto : activeEmployeeDtos) {
            try {
                // Get employee entity for database operations
                Employee employee = employeeService.getEmployeeEntityById(employeeDto.id());
                
                // Check if payslip already exists for this employee, month, and year
                if (payslipRepository.existsByEmployeeAndMonthAndYear(employee, month, year)) {
                    continue; // Skip this employee
                }

                // Get the current active employment for this employee using DTO
                EmploymentResponseDto employmentDto = employmentService.getCurrentEmployment(employeeDto.id());

                // Only generate payslip if employment is active
                if (employmentDto.status() == EmploymentStatus.ACTIVE) {
                    Payslip payslip = calculatePayslip(employee, employmentDto, month, year);
                    generatedPayslips.add(payslipRepository.save(payslip));
                }
            } catch (EntityNotFoundException e) {
                // Skip employees without active employment
                continue;
            }
        }

        return payslipMapper.toResponseDtoList(generatedPayslips);
    }

    @Transactional
    public List<PayslipResponseDto> approvePayslipsForMonth(Integer month, Integer year) {
        List<Payslip> pendingPayslips = payslipRepository.findByMonthAndYearAndStatus(month, year, PayslipStatus.PENDING);
        List<Payslip> approvedPayslips = new ArrayList<>();

        for (Payslip payslip : pendingPayslips) {
            payslip.setStatus(PayslipStatus.PAID);
            approvedPayslips.add(payslipRepository.save(payslip));
        }

        return payslipMapper.toResponseDtoList(approvedPayslips);
    }

    private Payslip calculatePayslip(Employee employee, EmploymentResponseDto employment, Integer month, Integer year) {
        BigDecimal baseSalary = employment.baseSalary();

        // Get deductions
        Deduction employeeTax = deductionService.getDeductionByName("Employee Tax");
        Deduction pension = deductionService.getDeductionByName("Pension");
        Deduction medicalInsurance = deductionService.getDeductionByName("Medical Insurance");
        Deduction others = deductionService.getDeductionByName("Others");
        Deduction housing = deductionService.getDeductionByName("Housing");
        Deduction transport = deductionService.getDeductionByName("Transport");

        // Calculate amounts
        BigDecimal houseAmount = calculatePercentage(baseSalary, housing.getPercentage());
        BigDecimal transportAmount = calculatePercentage(baseSalary, transport.getPercentage());
        BigDecimal employeeTaxAmount = calculatePercentage(baseSalary, employeeTax.getPercentage());
        BigDecimal pensionAmount = calculatePercentage(baseSalary, pension.getPercentage());
        BigDecimal medicalInsuranceAmount = calculatePercentage(baseSalary, medicalInsurance.getPercentage());
        BigDecimal otherTaxedAmount = calculatePercentage(baseSalary, others.getPercentage());

        // Calculate gross and net salary
        BigDecimal grossSalary = baseSalary.add(houseAmount).add(transportAmount);
        BigDecimal netSalary = grossSalary.subtract(employeeTaxAmount)
                .subtract(pensionAmount)
                .subtract(medicalInsuranceAmount)
                .subtract(otherTaxedAmount);

        // Create and return payslip
        return Payslip.builder()
                .employee(employee)
                .houseAmount(houseAmount)
                .transportAmount(transportAmount)
                .employeeTaxedAmount(employeeTaxAmount)
                .pensionAmount(pensionAmount)
                .medicalInsuranceAmount(medicalInsuranceAmount)
                .otherTaxedAmount(otherTaxedAmount)
                .grossSalary(grossSalary)
                .netSalary(netSalary)
                .month(month)
                .year(year)
                .status(PayslipStatus.PENDING)
                .build();
    }

    private BigDecimal calculatePercentage(BigDecimal amount, BigDecimal percentage) {
        return amount.multiply(percentage.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
