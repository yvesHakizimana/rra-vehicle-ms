package com.ne.rra_vehicle_ms.commons.validation.password;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Custom annotation for password validation
 *
 * Password requirements:
 * - Minimum 8 characters
 * - At least one uppercase letter
 * - At least one lowercase letter
 * - At least one digit
 * - At least one special character (@$!%*?&)
 * - No whitespace characters
 */
@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {

    String message() default "Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one digit, and one special character";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Minimum password length (default: 8)
     */
    int minLength() default 8;

    /**
     * Maximum password length (default: 128)
     */
    int maxLength() default 128;

    /**
     * Require at least one uppercase letter (default: true)
     */
    boolean requireUppercase() default true;

    /**
     * Require at least one lowercase letter (default: true)
     */
    boolean requireLowercase() default true;

    /**
     * Require at least one digit (default: true)
     */
    boolean requireDigit() default true;

    /**
     * Require at least one special character (default: true)
     */
    boolean requireSpecialChar() default true;

    /**
     * Allow whitespace characters (default: false)
     */
    boolean allowWhitespace() default false;

    /**
     * Custom special characters (default: @$!%*?&)
     */
    String specialChars() default "@$!%*?&";
}