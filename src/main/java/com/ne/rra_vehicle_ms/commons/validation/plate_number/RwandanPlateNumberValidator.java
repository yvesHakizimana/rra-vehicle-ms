package com.ne.rra_vehicle_ms.commons.validation.plate_number;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

/**
 * Custom validator for Rwandan vehicle plate numbers.
 * 
 * <p>This validator ensures that plate numbers conform to the official Rwandan vehicle 
 * registration format as defined by the Rwanda Revenue Authority (RRA).</p>
 * 
 * <h3>Supported Plate Number Formats:</h3>
 * <ul>
 *   <li><strong>Passenger Vehicles:</strong> R + 2 letters + 3 digits + 1 letter 
 *       <br>Example: RAA001A, RBB123B, RZZ999Z</li>
 *   <li><strong>Commercial Vehicles:</strong> IT + 3 digits + 1 letter 
 *       <br>Example: IT001A, IT999Z</li>
 *   <li><strong>Government Vehicles:</strong> GP + 3 digits + 1 letter 
 *       <br>Example: GP001A, GP999Z</li>
 *   <li><strong>Military Vehicles:</strong> RDF + 3 digits + 1 letter 
 *       <br>Example: RDF001A, RDF999Z</li>
 *   <li><strong>Police Vehicles:</strong> RNP + 3 digits + 1 letter 
 *       <br>Example: RNP001A, RNP999Z</li>
 * </ul>
 * 
 * <h3>Validation Rules:</h3>
 * <ul>
 *   <li>All letters must be uppercase (A-Z excluding O)</li>
 *   <li>Letter 'O' is excluded to avoid confusion with digit '0'</li>
 *   <li>Digits range from 0-9</li>
 *   <li>Total length varies by vehicle type (7-8 characters)</li>
 *   <li>Case-insensitive validation (automatically converts to uppercase)</li>
 * </ul>
 * 
 * <h3>Usage Example:</h3>
 * <pre>{@code
 * public class VehicleDto {
 *     @ValidRwandanPlateNumber(message = "Invalid Rwandan plate number format")
 *     private String plateNumber;
 * }
 * }</pre>
 * 
 * @author RRA Vehicle Management Team
 * @version 1.0.0
 * @since 2025-01-01
 */
@Slf4j
public class RwandanPlateNumberValidator implements ConstraintValidator<ValidRwandanPlateNumber, String> {

    /**
     * Regular expression pattern for validating Rwandan plate numbers.
     * 
     * <p><strong>Pattern Breakdown:</strong></p>
     * <ul>
     *   <li><code>R[A-NP-Z]{2}[0-9]{3}[A-NP-Z]</code> - Passenger vehicles
     *       <ul>
     *         <li>R - Fixed prefix for passenger vehicles</li>
     *         <li>[A-NP-Z]{2} - Two letters (excluding O)</li>
     *         <li>[0-9]{3} - Three digits</li>
     *         <li>[A-NP-Z] - One letter (excluding O)</li>
     *       </ul>
     *   </li>
     *   <li><code>(IT|GP|RDF|RNP)[0-9]{3}[A-NP-Z]</code> - Special purpose vehicles
     *       <ul>
     *         <li>IT - Commercial vehicles</li>
     *         <li>GP - Government vehicles</li>
     *         <li>RDF - Military vehicles (Rwanda Defence Force)</li>
     *         <li>RNP - Police vehicles (Rwanda National Police)</li>
     *         <li>[0-9]{3} - Three digits</li>
     *         <li>[A-NP-Z] - One letter (excluding O)</li>
     *       </ul>
     *   </li>
     * </ul>
     * 
     * <p><strong>Note:</strong> Letter 'O' is excluded from [A-NP-Z] to prevent confusion with digit '0'.</p>
     */
    private static final String PLATE_NUMBER_PATTERN =
            "^(" +
                    "R[A-NP-Z]{2}[0-9]{3}[A-NP-Z]|" +     // Passenger: RAA001A, RZZ999Z (no O)
                    "(IT|GP|RDF|RNP)[0-9]{3}[A-NP-Z]" +   // Commercial, Government, Military, Police
                    ")$";

    /**
     * Pre-compiled pattern for better performance.
     * Compiling the regex pattern once and reusing it is more efficient than 
     * compiling it every time validation is performed.
     */
    private static final java.util.regex.Pattern COMPILED_PATTERN = 
            java.util.regex.Pattern.compile(PLATE_NUMBER_PATTERN);

    /**
     * Initializes the validator. Called once when the validator instance is created.
     * 
     * @param constraintAnnotation the annotation instance for a given constraint declaration
     */
    @Override
    public void initialize(ValidRwandanPlateNumber constraintAnnotation) {
        log.debug("Initializing Rwandan Plate Number Validator");
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    /**
     * Validates a Rwandan plate number against the official format.
     * 
     * <p>This method performs the following validations:</p>
     * <ol>
     *   <li>Checks if the input is null (returns true to let @NotNull handle null validation)</li>
     *   <li>Converts input to uppercase for case-insensitive validation</li>
     *   <li>Validates against the Rwandan plate number pattern</li>
     *   <li>Logs validation attempts for debugging purposes</li>
     * </ol>
     * 
     * <h3>Valid Examples:</h3>
     * <ul>
     *   <li>RAA001A - Passenger vehicle</li>
     *   <li>rbb123b - Passenger vehicle (case insensitive)</li>
     *   <li>IT001A - Commercial vehicle</li>
     *   <li>GP999Z - Government vehicle</li>
     *   <li>RDF001A - Military vehicle</li>
     *   <li>RNP999Z - Police vehicle</li>
     * </ul>
     * 
     * <h3>Invalid Examples:</h3>
     * <ul>
     *   <li>RAO001A - Contains letter 'O'</li>
     *   <li>RA001A - Missing one letter</li>
     *   <li>RAA01A - Missing one digit</li>
     *   <li>XYZ123A - Invalid prefix</li>
     *   <li>RAA1234A - Too many digits</li>
     * </ul>
     * 
     * @param plateNumber the plate number to validate (can be null)
     * @param context provides contextual data and operation when applying a given constraint validator
     * @return true if the plate number is valid or null, false otherwise
     * 
     * @see ValidRwandanPlateNumber
     */
    @Override
    public boolean isValid(String plateNumber, ConstraintValidatorContext context) {
        // Handle null values - let @NotNull annotation handle null validation
        if (plateNumber == null) {
            log.debug("Plate number validation: null value received, delegating to @NotNull");
            return true; 
        }

        // Trim whitespace and convert to uppercase for consistent validation
        String normalizedPlateNumber = plateNumber.trim().toUpperCase();
        
        // Log validation attempt for debugging
        log.debug("Validating plate number: '{}' (normalized: '{}')", plateNumber, normalizedPlateNumber);
        
        // Validate against the pattern
        boolean isValid = COMPILED_PATTERN.matcher(normalizedPlateNumber).matches();
        
        // Log validation result
        if (isValid) {
            log.debug("Plate number '{}' is valid", normalizedPlateNumber);
        } else {
            log.debug("Plate number '{}' is invalid - does not match Rwandan format", normalizedPlateNumber);
            
            // Provide detailed error message for development
            addDetailedErrorMessage(context, normalizedPlateNumber);
        }
        
        return isValid;
    }

    /**
     * Adds a detailed error message to help developers understand validation failures.
     * 
     * @param context the constraint validator context
     * @param plateNumber the invalid plate number
     */
    private void addDetailedErrorMessage(ConstraintValidatorContext context, String plateNumber) {
        // Disable default error message
        context.disableDefaultConstraintViolation();
        
        // Determine the specific reason for failure
        String errorMessage = determineErrorReason(plateNumber);
        
        // Add a custom error message
        context.buildConstraintViolationWithTemplate(errorMessage)
               .addConstraintViolation();
    }

    /**
     * Determines the specific reason why a plate number is invalid.
     * 
     * @param plateNumber the invalid plate number
     * @return a descriptive error message
     */
    private String determineErrorReason(String plateNumber) {
        if (plateNumber.isEmpty()) {
            return "Plate number cannot be empty";
        }
        
        if (plateNumber.contains("O") || plateNumber.contains("o")) {
            return "Plate number cannot contain letter 'O' - use other letters to avoid confusion with digit '0'";
        }
        
        if (plateNumber.length() < 7) {
            return "Plate number is too short - minimum 7 characters required";
        }
        
        if (plateNumber.length() > 8) {
            return "Plate number is too long - maximum 8 characters allowed";
        }
        
        if (!plateNumber.matches("^[A-Z0-9]+$")) {
            return "Plate number can only contain letters and digits";
        }
        
        // Check specific prefixes
        if (plateNumber.startsWith("R") && plateNumber.length() == 7) {
            return "Passenger vehicle plate must follow format: R + 2 letters + 3 digits + 1 letter (e.g., RAA001A)";
        }
        
        if (plateNumber.length() == 8) {
            String prefix = plateNumber.substring(0, 3);
            if (!prefix.matches("^(IT|GP|RDF|RNP)$")) {
                return "Special vehicle plate must start with IT, GP, RDF, or RNP followed by 3 digits and 1 letter";
            }
        }
        
        return "Invalid Rwandan plate number format. Expected formats: RAA001A (passenger), IT001A (commercial), GP001A (government), RDF001A (military), RNP001A (police)";
    }

    /**
     * Utility method to check if a plate number is for a passenger vehicle.
     * 
     * @param plateNumber the plate number to check
     * @return true if it's a passenger vehicle plate number
     */
    public static boolean isPassengerVehicle(String plateNumber) {
        if (plateNumber == null) return false;
        return plateNumber.toUpperCase().matches("^R[A-NP-Z]{2}[0-9]{3}[A-NP-Z]$");
    }

    /**
     * Utility method to check if a plate number is for a commercial vehicle.
     * 
     * @param plateNumber the plate number to check
     * @return true if it's a commercial vehicle plate number
     */
    public static boolean isCommercialVehicle(String plateNumber) {
        if (plateNumber == null) return false;
        return plateNumber.toUpperCase().matches("^IT[0-9]{3}[A-NP-Z]$");
    }

    /**
     * Utility method to check if a plate number is for a government vehicle.
     * 
     * @param plateNumber the plate number to check
     * @return true if it's a government vehicle plate number
     */
    public static boolean isGovernmentVehicle(String plateNumber) {
        if (plateNumber == null) return false;
        return plateNumber.toUpperCase().matches("^GP[0-9]{3}[A-NP-Z]$");
    }

    /**
     * Utility method to check if a plate number is for a military vehicle.
     * 
     * @param plateNumber the plate number to check
     * @return true if it's a military vehicle plate number
     */
    public static boolean isMilitaryVehicle(String plateNumber) {
        if (plateNumber == null) return false;
        return plateNumber.toUpperCase().matches("^RDF[0-9]{3}[A-NP-Z]$");
    }

    /**
     * Utility method to check if a plate number is for a police vehicle.
     * 
     * @param plateNumber the plate number to check
     * @return true if it's a police vehicle plate number
     */
    public static boolean isPoliceVehicle(String plateNumber) {
        if (plateNumber == null) return false;
        return plateNumber.toUpperCase().matches("^RNP[0-9]{3}[A-NP-Z]$");
    }

    /**
     * Utility method to determine the vehicle type from a plate number.
     * 
     * @param plateNumber the plate number to analyze
     * @return the vehicle type as a string
     */
    public static String getVehicleType(String plateNumber) {
        if (plateNumber == null) return "UNKNOWN";
        
        String normalized = plateNumber.toUpperCase();
        
        if (isPassengerVehicle(normalized)) return "PASSENGER";
        if (isCommercialVehicle(normalized)) return "COMMERCIAL";
        if (isGovernmentVehicle(normalized)) return "GOVERNMENT";
        if (isMilitaryVehicle(normalized)) return "MILITARY";
        if (isPoliceVehicle(normalized)) return "POLICE";
        
        return "UNKNOWN";
    }
}