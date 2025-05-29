package com.ne.rra_vehicle_ms.deductions.repositories;

import com.ne.rra_vehicle_ms.deductions.entities.Deduction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeductionRepository extends JpaRepository<Deduction, UUID> {
    Optional<Deduction> findByCode(String code);
    Optional<Deduction> findByDeductionName(String deductionName);
    boolean existsByCode(String code);
    boolean existsByDeductionName(String deductionName);
}