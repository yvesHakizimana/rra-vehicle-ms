package com.ne.rra_vehicle_ms.commons.validation.password;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/**
 * Validator for @ValidPassword annotation
 */
public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    private int minLength;
    private int maxLength;
    private boolean requireUppercase;
    private boolean requireLowercase;
    private boolean requireDigit;
    private boolean requireSpecialChar;
    private boolean allowWhitespace;
    private String specialChars;

    // Compiled regex patterns for performance
    private Pattern uppercasePattern;
    private Pattern lowercasePattern;
    private Pattern digitPattern;
    private Pattern specialCharPattern;
    private Pattern whitespacePattern;

    @Override
    public void initialize(ValidPassword constraintAnnotation) {
        this.minLength = constraintAnnotation.minLength();
        this.maxLength = constraintAnnotation.maxLength();
        this.requireUppercase = constraintAnnotation.requireUppercase();
        this.requireLowercase = constraintAnnotation.requireLowercase();
        this.requireDigit = constraintAnnotation.requireDigit();
        this.requireSpecialChar = constraintAnnotation.requireSpecialChar();
        this.allowWhitespace = constraintAnnotation.allowWhitespace();
        this.specialChars = constraintAnnotation.specialChars();

        // Compile regex patterns
        this.uppercasePattern = Pattern.compile(".*[A-Z].*");
        this.lowercasePattern = Pattern.compile(".*[a-z].*");
        this.digitPattern = Pattern.compile(".*\\d.*");
        this.specialCharPattern = Pattern.compile(".*[" + Pattern.quote(specialChars) + "].*");
        this.whitespacePattern = Pattern.compile(".*\\s.*");
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        // Null passwords are handled by @NotNull annotation
        if (password == null) {
            return true;
        }

        // Build custom error message based on failed validations
        StringBuilder errorMessage = new StringBuilder();
        boolean isValid = true;

        // Check length
        if (password.length() < minLength) {
            errorMessage.append("Password must be at least ").append(minLength).append(" characters long. ");
            isValid = false;
        }

        if (password.length() > maxLength) {
            errorMessage.append("Password must not exceed ").append(maxLength).append(" characters. ");
            isValid = false;
        }

        // Check uppercase requirement
        if (requireUppercase && !uppercasePattern.matcher(password).matches()) {
            errorMessage.append("Password must contain at least one uppercase letter. ");
            isValid = false;
        }

        // Check lowercase requirement
        if (requireLowercase && !lowercasePattern.matcher(password).matches()) {
            errorMessage.append("Password must contain at least one lowercase letter. ");
            isValid = false;
        }

        // Check digit requirement
        if (requireDigit && !digitPattern.matcher(password).matches()) {
            errorMessage.append("Password must contain at least one digit. ");
            isValid = false;
        }

        // Check special character requirement
        if (requireSpecialChar && !specialCharPattern.matcher(password).matches()) {
            errorMessage.append("Password must contain at least one special character (")
                    .append(specialChars).append("). ");
            isValid = false;
        }

        // Check whitespace restriction
        if (!allowWhitespace && whitespacePattern.matcher(password).matches()) {
            errorMessage.append("Password must not contain whitespace characters. ");
            isValid = false;
        }

        // Set custom error message if validation failed
        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(errorMessage.toString().trim())
                    .addConstraintViolation();
        }

        return isValid;
    }
}