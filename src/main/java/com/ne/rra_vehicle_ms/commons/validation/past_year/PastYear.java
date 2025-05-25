package com.ne.rra_vehicle_ms.commons.validation.past_year;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = PastYearValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface PastYear {
    String message() default "Year of manufacture must be in the past.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}