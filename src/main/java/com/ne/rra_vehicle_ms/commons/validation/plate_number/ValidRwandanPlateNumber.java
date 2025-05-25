package com.ne.rra_vehicle_ms.commons.validation.plate_number;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = RwandanPlateNumberValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidRwandanPlateNumber {
    String message() default "Invalid Rwandan plate number format.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}