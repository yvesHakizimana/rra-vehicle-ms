package com.ne.rra_vehicle_ms.commons.validation.past_year;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.Year;

public class PastYearValidator implements ConstraintValidator<PastYear, Integer> {
    @Override
    public boolean isValid(Integer year, ConstraintValidatorContext context) {
        if (year == null) {
            return true; // Let @NotNull handle null validation
        }
        int currentYear = Year.now().getValue(); // Get current year (2025)
        return year <= currentYear; // Year must be before or equal to 2025
    }
}