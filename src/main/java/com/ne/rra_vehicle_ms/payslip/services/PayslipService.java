package com.ne.rra_vehicle_ms.payslip.services;

import com.ne.rra_vehicle_ms.deductions.entities.Deduction;
import com.ne.rra_vehicle_ms.deductions.services.DeductionService;
import com.ne.rra_vehicle_ms.employee.entities.Employee;
import com.ne.rra_vehicle_ms.employee.entities.EmployeeStatus;
import com.ne.rra_vehicle_ms.employee.services.EmployeeService;
import com.ne.rra_vehicle_ms.employment.entities.Employment;
import com.ne.rra_vehicle_ms.employment.entities.EmploymentStatus;
import com.ne.rra_vehicle_ms.employment.services.EmploymentService;
import com.ne.rra_vehicle_ms.payslip.entities.Payslip;
import com.ne.rra_vehicle_ms.payslip.entities.PayslipStatus;
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

    @Transactional
    public Payslip createPayslip(Payslip payslip) {
        // Check if payslip for the same employee, month, and year already exists
        if (payslipRepository.existsByEmployeeAndMonthAndYear(
                payslip.getEmployee(), payslip.getMonth(), payslip.getYear())) {
            throw new EntityExistsException("Payslip for employee " + payslip.getEmployee().getCode() +
                    " for month " + payslip.getMonth() + " and year " + payslip.getYear() + " already exists");
        }
        
        return payslipRepository.save(payslip);
    }

    @Transactional(readOnly = true)
    public Payslip getPayslipById(UUID id) {
        return payslipRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payslip with id " + id + " not found"));
    }

    @Transactional(readOnly = true)
    public List<Payslip> getPayslipsByEmployee(UUID employeeId) {
        Employee employee = employeeService.getEmployeeById(employeeId);
        return payslipRepository.findByEmployee(employee);
    }

    @Transactional(readOnly = true)
    public List<Payslip> getPayslipsByEmployeeAndStatus(UUID employeeId, PayslipStatus status) {
        Employee employee = employeeService.getEmployeeById(employeeId);
        return payslipRepository.findByEmployeeAndStatus(employee, status);
    }

    @Transactional(readOnly = true)
    public List<Payslip> getPayslipsByStatus(PayslipStatus status) {
        return payslipRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<Payslip> getPayslipsByMonthAndYear(Integer month, Integer year) {
        return payslipRepository.findByMonthAndYear(month, year);
    }

    @Transactional(readOnly = true)
    public List<Payslip> getPayslipsByMonthAndYearAndStatus(Integer month, Integer year, PayslipStatus status) {
        return payslipRepository.findByMonthAndYearAndStatus(month, year, status);
    }

    @Transactional(readOnly = true)
    public Payslip getPayslipByEmployeeAndMonthAndYear(UUID employeeId, Integer month, Integer year) {
        Employee employee = employeeService.getEmployeeById(employeeId);
        return payslipRepository.findByEmployeeAndMonthAndYear(employee, month, year)
                .orElseThrow(() -> new EntityNotFoundException("Payslip for employee " + employee.getCode() +
                        " for month " + month + " and year " + year + " not found"));
    }

    @Transactional
    public Payslip updatePayslipStatus(UUID id, PayslipStatus status) {
        Payslip payslip = getPayslipById(id);
        payslip.setStatus(status);
        return payslipRepository.save(payslip);
    }

    @Transactional
    public List<Payslip> generatePayslipsForMonth(Integer month, Integer year) {
        // Get all active employees
        List<Employee> activeEmployees = employeeService.getEmployeesByStatus(EmployeeStatus.ACTIVE);
        List<Payslip> generatedPayslips = new ArrayList<>();
        
        for (Employee employee : activeEmployees) {
            try {
                // Check if payslip already exists for this employee, month, and year
                if (payslipRepository.existsByEmployeeAndMonthAndYear(employee, month, year)) {
                    continue; // Skip this employee
                }
                
                // Get the current active employment for this employee
                Employment employment = employmentService.getCurrentEmployment(employee.getId());
                
                // Only generate payslip if employment is active
                if (employment.getStatus() == EmploymentStatus.ACTIVE) {
                    Payslip payslip = calculatePayslip(employee, employment, month, year);
                    generatedPayslips.add(payslipRepository.save(payslip));
                }
            } catch (EntityNotFoundException e) {
                // Skip employees without active employment
                continue;
            }
        }
        
        return generatedPayslips;
    }
    
    @Transactional
    public List<Payslip> approvePayslipsForMonth(Integer month, Integer year) {
        List<Payslip> pendingPayslips = payslipRepository.findByMonthAndYearAndStatus(month, year, PayslipStatus.PENDING);
        List<Payslip> approvedPayslips = new ArrayList<>();
        
        for (Payslip payslip : pendingPayslips) {
            payslip.setStatus(PayslipStatus.PAID);
            approvedPayslips.add(payslipRepository.save(payslip));
        }
        
        return approvedPayslips;
    }
    
    private Payslip calculatePayslip(Employee employee, Employment employment, Integer month, Integer year) {
        BigDecimal baseSalary = employment.getBaseSalary();
        
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