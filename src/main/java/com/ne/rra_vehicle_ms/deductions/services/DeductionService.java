package com.ne.rra_vehicle_ms.deductions.services;

import com.ne.rra_vehicle_ms.deductions.entities.Deduction;
import com.ne.rra_vehicle_ms.deductions.repositories.DeductionRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeductionService {
    private final DeductionRepository deductionRepository;

    @Transactional
    public Deduction createDeduction(Deduction deduction) {
        // Check if deduction with same code or name already exists
        if (deduction.getCode() != null && deductionRepository.existsByCode(deduction.getCode())) {
            throw new EntityExistsException("Deduction with code " + deduction.getCode() + " already exists");
        }
        if (deductionRepository.existsByDeductionName(deduction.getDeductionName())) {
            throw new EntityExistsException("Deduction with name " + deduction.getDeductionName() + " already exists");
        }
        
        // Validate percentage (should be between 0 and 100)
        validatePercentage(deduction.getPercentage());
        
        return deductionRepository.save(deduction);
    }

    @Transactional(readOnly = true)
    public Deduction getDeductionById(UUID id) {
        return deductionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Deduction with id " + id + " not found"));
    }

    @Transactional(readOnly = true)
    public Deduction getDeductionByCode(String code) {
        return deductionRepository.findByCode(code)
                .orElseThrow(() -> new EntityNotFoundException("Deduction with code " + code + " not found"));
    }

    @Transactional(readOnly = true)
    public Deduction getDeductionByName(String name) {
        return deductionRepository.findByDeductionName(name)
                .orElseThrow(() -> new EntityNotFoundException("Deduction with name " + name + " not found"));
    }

    @Transactional(readOnly = true)
    public List<Deduction> getAllDeductions() {
        return deductionRepository.findAll();
    }

    @Transactional
    public Deduction updateDeduction(UUID id, Deduction deductionDetails) {
        Deduction deduction = getDeductionById(id);
        
        // Only update name if it's different and not already taken
        if (!deduction.getDeductionName().equals(deductionDetails.getDeductionName())) {
            if (deductionRepository.existsByDeductionName(deductionDetails.getDeductionName())) {
                throw new EntityExistsException("Deduction name " + deductionDetails.getDeductionName() + " is already in use");
            }
            deduction.setDeductionName(deductionDetails.getDeductionName());
        }
        
        // Validate and update percentage
        validatePercentage(deductionDetails.getPercentage());
        deduction.setPercentage(deductionDetails.getPercentage());
        
        return deductionRepository.save(deduction);
    }

    @Transactional
    public void deleteDeduction(UUID id) {
        Deduction deduction = getDeductionById(id);
        deductionRepository.delete(deduction);
    }
    
    private void validatePercentage(BigDecimal percentage) {
        if (percentage == null) {
            throw new IllegalArgumentException("Percentage cannot be null");
        }
        if (percentage.compareTo(BigDecimal.ZERO) < 0 || percentage.compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException("Percentage must be between 0 and 100");
        }
    }
}