package com.ne.rra_vehicle_ms.commons.validation.national_id;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.time.Year;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Custom validator for Rwandan National ID numbers.
 *
 * <p>This validator implements the Jakarta {@link ConstraintValidator} interface to validate
 * Rwandan National ID numbers according to the official format specifications established
 * by the National Identification Agency (NIDA) of Rwanda.</p>
 *
 * <h3>National ID Structure:</h3>
 * <p>A valid Rwandan National ID consists of <strong>16 digits</strong> divided into 6 distinct functional groups:</p>
 *
 * <pre>
 * Format: <strong>G-YYYY-#-NNNNNNN-I-CC</strong>
 *
 * Where:
 * <strong>G</strong>       - National Identifier (1 digit)
 *           1 = Rwandan citizen
 *           2 = Refugee with issued ID
 *           3 = Foreigner with issued ID
 *
 * <strong>YYYY</strong>    - Year of Birth (4 digits)
 *           Must result in age between 16-120 years at time of validation
 *
 * <strong>#</strong>       - Gender Identifier (1 digit)
 *           7 = Female
 *           8 = Male
 *
 * <strong>NNNNNNN</strong> - Birth Order Number (7 digits)
 *           Sequential order of ID issuance for people born in the same year
 *           Cannot be all zeros (0000000)
 *
 * <strong>I</strong>       - Issue Frequency (1 digit)
 *           0 = First time issued
 *           1-9 = Number of times the ID has been reissued
 *
 * <strong>CC</strong>      - Security Code (2 digits)
 *           Unique checksum for anti-counterfeiting (00-99)
 * </pre>
 *
 * <h3>Valid ID Examples:</h3>
 * <ul>
 *   <li><strong>1199880123456701</strong> - Rwandan male born 1998, first issuance</li>
 *   <li><strong>2200070987654302</strong> - Refugee female born 2000, second issuance</li>
 *   <li><strong>3198580000123450</strong> - Foreigner male born 1985, first issuance</li>
 * </ul>
 *
 * <h3>Usage Example:</h3>
 * <pre>{@code
 * public class PersonDto {
 *     @ValidRwandaId(message = "Invalid Rwandan National ID format")
 *     private String nationalId;
 * }
 * }</pre>
 *
 * <h3>Business Rules:</h3>
 * <ul>
 *   <li>Total length must be exactly 16 digits</li>
 *   <li>Age at validation must be between 16-120 years</li>
 *   <li>Birth order cannot be all zeros</li>
 *   <li>All characters must be numeric digits (0-9)</li>
 *   <li>Gender must be either 7 (female) or 8 (male)</li>
 *   <li>Nationality must be 1, 2, or 3</li>
 * </ul>
 *
 * @author RRA Vehicle Management Team
 * @version 2.0.0
 * @since 2025-01-01
 * @see ValidRwandaId
 * @see <a href="https://nida.gov.rw">National Identification Agency (NIDA)</a>
 */
@Slf4j
public class RwandaNationalIdValidator implements ConstraintValidator<ValidRwandaId, String> {

    /**
     * Regular expression pattern for validating the structure of a Rwandan National ID.
     *
     * <p><strong>Pattern Breakdown:</strong></p>
     * <ul>
     *   <li><code>(\\d)</code> - Group 1: National Identifier (1 digit)</li>
     *   <li><code>(\\d{4})</code> - Group 2: Year of Birth (4 digits)</li>
     *   <li><code>([78])</code> - Group 3: Gender Identifier (7 or 8)</li>
     *   <li><code>(\\d{7})</code> - Group 4: Birth Order Number (7 digits)</li>
     *   <li><code>(\\d)</code> - Group 5: Issue Frequency (1 digit)</li>
     *   <li><code>(\\d{2})</code> - Group 6: Security Code (2 digits)</li>
     * </ul>
     */
    private static final String ID_PATTERN = "^(\\d)(\\d{4})([78])(\\d{7})(\\d)(\\d{2})$";

    /**
     * Pre-compiled pattern for better performance.
     * Compiling the regex pattern once and reusing it is more efficient than
     * compiling it every time validation is performed.
     */
    private static final Pattern COMPILED_PATTERN = Pattern.compile(ID_PATTERN);

    /** Minimum age required for ID issuance (16 years) */
    private static final int MIN_ISSUANCE_AGE = 16;

    /** Maximum reasonable age for ID validation (120 years) */
    private static final int MAX_ISSUANCE_AGE = 120;

    /** Expected length of a valid Rwandan National ID */
    private static final int EXPECTED_ID_LENGTH = 16;

    /**
     * Nationality codes as defined by NIDA.
     */
    public enum Nationality {
        RWANDAN(1, "Rwandan Citizen"),
        REFUGEE(2, "Refugee with Issued ID"),
        FOREIGNER(3, "Foreigner with Issued ID");

        private final int code;
        private final String description;

        Nationality(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public int getCode() { return code; }
        public String getDescription() { return description; }

        public static Nationality fromCode(int code) {
            for (Nationality nat : values()) {
                if (nat.code == code) return nat;
            }
            return null;
        }
    }

    /**
     * Gender codes as defined by NIDA.
     */
    public enum Gender {
        FEMALE(7, "Female"),
        MALE(8, "Male");

        private final int code;
        private final String description;

        Gender(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public int getCode() { return code; }
        public String getDescription() { return description; }

        public static Gender fromCode(int code) {
            for (Gender gender : values()) {
                if (gender.code == code) return gender;
            }
            return null;
        }
    }

    /**
     * Initializes the validator. Called once when the validator instance is created.
     *
     * @param constraintAnnotation the annotation instance for a given constraint declaration
     */
    @Override
    public void initialize(ValidRwandaId constraintAnnotation) {
        log.debug("Initializing Rwanda National ID Validator");
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    /**
     * Validates a Rwandan National ID string against the official format and business rules.
     *
     * <p>This method performs comprehensive validation including:</p>
     * <ol>
     *   <li>Null and empty string validation</li>
     *   <li>Length validation (must be exactly 16 digits)</li>
     *   <li>Pattern structure validation</li>
     *   <li>Individual component validation (nationality, birth year, gender, etc.)</li>
     *   <li>Business rule validation (age constraints, birth order rules)</li>
     * </ol>
     *
     * @param nationalId the National ID string to validate (can be null)
     * @param context provides contextual data and operation when applying the constraint validator
     * @return true if the National ID is valid, false otherwise
     */
    @Override
    public boolean isValid(String nationalId, ConstraintValidatorContext context) {
        // Handle null values - let @NotNull annotation handle null validation
        if (nationalId == null) {
            log.debug("National ID validation: null value received, delegating to @NotNull");
            return true;
        }

        // Trim whitespace for consistent validation
        String trimmedId = nationalId.trim();

        log.debug("Validating National ID: '{}'", maskNationalId(trimmedId));

        // Basic length validation
        if (trimmedId.isEmpty()) {
            addCustomErrorMessage(context, "National ID cannot be empty");
            return false;
        }

        if (trimmedId.length() != EXPECTED_ID_LENGTH) {
            addCustomErrorMessage(context,
                    String.format("National ID must be exactly %d digits long, but received %d digits",
                            EXPECTED_ID_LENGTH, trimmedId.length()));
            return false;
        }

        // Pattern structure validation
        Matcher matcher = COMPILED_PATTERN.matcher(trimmedId);
        if (!matcher.matches()) {
            addCustomErrorMessage(context,
                    "National ID format is invalid. Expected format: GYYYY#NNNNNNNIFCC where G=nationality, YYYY=birth year, #=gender(7/8), NNNNNNN=birth order, I=issue frequency, CC=security code");
            return false;
        }

        // Extract and validate individual components
        try {
            return validateComponents(matcher, context);
        } catch (Exception e) {
            log.error("Unexpected error during National ID validation: {}", e.getMessage());
            addCustomErrorMessage(context, "Invalid National ID format");
            return false;
        }
    }

    /**
     * Validates all individual components of the National ID.
     *
     * @param matcher the regex matcher containing the extracted groups
     * @param context the constraint validator context
     * @return true if all components are valid, false otherwise
     */
    private boolean validateComponents(Matcher matcher, ConstraintValidatorContext context) {
        String nationalityDigit = matcher.group(1);
        String birthYearStr = matcher.group(2);
        String genderDigit = matcher.group(3);
        String birthOrderDigits = matcher.group(4);
        String issueFrequencyDigit = matcher.group(5);
        String securityCodeDigits = matcher.group(6);

        // Validate nationality
        if (!isValidNationality(nationalityDigit)) {
            addCustomErrorMessage(context,
                    String.format("Invalid nationality code '%s'. Valid codes: 1=Rwandan, 2=Refugee, 3=Foreigner",
                            nationalityDigit));
            return false;
        }

        // Validate birth year
        if (!isValidBirthYear(birthYearStr)) {
            int year = Integer.parseInt(birthYearStr);
            int currentYear = Year.now().getValue();
            int age = currentYear - year;
            addCustomErrorMessage(context,
                    String.format("Invalid birth year '%s'. Age (%d years) must be between %d and %d years",
                            birthYearStr, age, MIN_ISSUANCE_AGE, MAX_ISSUANCE_AGE));
            return false;
        }

        // Validate gender
        if (!isValidGender(genderDigit)) {
            addCustomErrorMessage(context,
                    String.format("Invalid gender code '%s'. Valid codes: 7=Female, 8=Male", genderDigit));
            return false;
        }

        // Validate birth order
        if (!isValidBirthOrder(birthOrderDigits)) {
            addCustomErrorMessage(context,
                    "Invalid birth order number. Must be 7 digits and cannot be all zeros");
            return false;
        }

        // Validate issue frequency
        if (!isValidIssueFrequency(issueFrequencyDigit)) {
            addCustomErrorMessage(context,
                    String.format("Invalid issue frequency '%s'. Must be a single digit (0-9)",
                            issueFrequencyDigit));
            return false;
        }

        // Validate security code
        if (!isValidSecurityCode(securityCodeDigits)) {
            addCustomErrorMessage(context,
                    String.format("Invalid security code '%s'. Must be exactly 2 digits", securityCodeDigits));
            return false;
        }

        log.debug("National ID validation successful for ID: {}", maskNationalId(matcher.group(0)));
        return true;
    }

    /**
     * Validates the National Identifier digit (Group 1).
     *
     * <p>The first group consists of a single digit that identifies the holder's status:</p>
     * <ul>
     *   <li><strong>1</strong> - Rwandan citizens</li>
     *   <li><strong>2</strong> - Refugees who have been issued an ID</li>
     *   <li><strong>3</strong> - Foreigners with issued ID</li>
     * </ul>
     *
     * @param digit the nationality digit to validate
     * @return true if valid (1, 2, or 3), false otherwise
     */
    private boolean isValidNationality(String digit) {
        try {
            int code = Integer.parseInt(digit);
            return Nationality.fromCode(code) != null;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validates the Year of Birth (Group 2).
     *
     * <p>The second group comprises four digits that correspond to the year of birth of the ID holder.
     * The age at validation must be between 16 and 120 years (reasonable limits).</p>
     *
     * <p><strong>Business Rules:</strong></p>
     * <ul>
     *   <li>Minimum age: 16 years (legal age for ID issuance)</li>
     *   <li>Maximum age: 120 years (reasonable upper limit)</li>
     *   <li>Birth year must be numeric and valid</li>
     * </ul>
     *
     * @param yearStr the birth year string to validate (4 digits)
     * @return true if valid birth year resulting in reasonable age, false otherwise
     */
    private boolean isValidBirthYear(String yearStr) {
        try {
            int birthYear = Integer.parseInt(yearStr);
            int currentYear = Year.now().getValue();
            int ageAtValidation = currentYear - birthYear;

            return ageAtValidation >= MIN_ISSUANCE_AGE && ageAtValidation <= MAX_ISSUANCE_AGE;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validates the Gender Identifier (Group 3).
     *
     * <p>The third group is a single digit indicating the gender of the ID holder:</p>
     * <ul>
     *   <li><strong>7</strong> - Female</li>
     *   <li><strong>8</strong> - Male</li>
     * </ul>
     *
     * @param digit the gender digit to validate
     * @return true if valid (7 or 8), false otherwise
     */
    private boolean isValidGender(String digit) {
        try {
            int code = Integer.parseInt(digit);
            return Gender.fromCode(code) != null;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validates the Birth Order Number (Group 4).
     *
     * <p>The fourth group consists of seven digits that represent the sequential order
     * in which the ID was issued to individuals born in the same year.
     * This number indicates how many people of the same gender born in that specific year
     * have registered in the country.</p>
     *
     * <p><strong>Business Rules:</strong></p>
     * <ul>
     *   <li>Must be exactly 7 digits</li>
     *   <li>Cannot be all zeros (0000000)</li>
     *   <li>Represents chronological order of ID issuance</li>
     * </ul>
     *
     * @param digits the birth order digits to validate (7 digits)
     * @return true if valid (7 digits, not all zeros), false otherwise
     */
    private boolean isValidBirthOrder(String digits) {
        // Must be seven digits and not all zeros
        return digits.matches("\\d{7}") && !digits.equals("0000000");
    }

    /**
     * Validates the Issue Frequency (Group 5).
     *
     * <p>The fifth group contains a single digit indicating the number of times the ID
     * has been issued. This helps track the issuance history of the ID card:</p>
     * <ul>
     *   <li><strong>0</strong> - First time the ID is issued to the holder</li>
     *   <li><strong>1-9</strong> - Number of times the ID has been reissued</li>
     * </ul>
     *
     * @param digit the issue frequency digit to validate
     * @return true if valid (single digit 0-9), false otherwise
     */
    private boolean isValidIssueFrequency(String digit) {
        return digit.matches("\\d"); // Single digit 0-9
    }

    /**
     * Validates the Security Code (Group 6).
     *
     * <p>The sixth and final group is composed of two digits. These digits form a unique
     * checksum that serves as a security feature to prevent counterfeiting.
     * The specific algorithm for this checksum is known only to the National Identification Agency (NIDA).</p>
     *
     * @param digits the security code digits to validate (2 digits)
     * @return true if valid (exactly 2 digits), false otherwise
     */
    private boolean isValidSecurityCode(String digits) {
        return digits.matches("\\d{2}"); // Exactly 2 digits 00-99
    }

    /**
     * Adds a custom error message to the validation context.
     *
     * @param context the constraint validator context
     * @param message the custom error message
     */
    private void addCustomErrorMessage(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }

    /**
     * Masks a National ID for logging purposes to protect privacy.
     * Shows only the first 4 and last 2 digits.
     *
     * @param nationalId the National ID to mask
     * @return masked National ID for safe logging
     */
    private String maskNationalId(String nationalId) {
        if (nationalId == null || nationalId.length() < 6) {
            return "****";
        }
        return nationalId.substring(0, 4) + "**********" + nationalId.substring(nationalId.length() - 2);
    }

    // ========== UTILITY METHODS FOR DEVELOPERS ==========

    /**
     * Utility method to extract nationality from a valid National ID.
     *
     * @param nationalId the National ID to analyze
     * @return the nationality enum, or null if invalid
     */
    public static Nationality extractNationality(String nationalId) {
        if (nationalId == null || nationalId.length() != EXPECTED_ID_LENGTH) return null;

        try {
            int code = Character.getNumericValue(nationalId.charAt(0));
            return Nationality.fromCode(code);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Utility method to extract birth year from a valid National ID.
     *
     * @param nationalId the National ID to analyze
     * @return the birth year as integer, or -1 if invalid
     */
    public static int extractBirthYear(String nationalId) {
        if (nationalId == null || nationalId.length() != EXPECTED_ID_LENGTH) return -1;

        try {
            return Integer.parseInt(nationalId.substring(1, 5));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Utility method to extract gender from a valid National ID.
     *
     * @param nationalId the National ID to analyze
     * @return the gender enum, or null if invalid
     */
    public static Gender extractGender(String nationalId) {
        if (nationalId == null || nationalId.length() != EXPECTED_ID_LENGTH) return null;

        try {
            int code = Character.getNumericValue(nationalId.charAt(5));
            return Gender.fromCode(code);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Utility method to calculate age from a valid National ID.
     *
     * @param nationalId the National ID to analyze
     * @return the calculated age, or -1 if invalid
     */
    public static int calculateAge(String nationalId) {
        int birthYear = extractBirthYear(nationalId);
        if (birthYear == -1) return -1;

        return Year.now().getValue() - birthYear;
    }

    /**
     * Utility method to extract issue frequency from a valid National ID.
     *
     * @param nationalId the National ID to analyze
     * @return the issue frequency (0-9), or -1 if invalid
     */
    public static int extractIssueFrequency(String nationalId) {
        if (nationalId == null || nationalId.length() != EXPECTED_ID_LENGTH) return -1;

        try {
            return Character.getNumericValue(nationalId.charAt(13));
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Utility method to check if a National ID belongs to a first-time issuance.
     *
     * @param nationalId the National ID to check
     * @return true if first-time issuance (issue frequency = 0), false otherwise
     */
    public static boolean isFirstTimeIssuance(String nationalId) {
        return extractIssueFrequency(nationalId) == 0;
    }

    /**
     * Utility method to format a National ID for display purposes.
     * Formats as: G-YYYY-#-NNNNNNN-I-CC
     *
     * @param nationalId the National ID to format
     * @return formatted National ID, or original string if invalid format
     */
    public static String formatForDisplay(String nationalId) {
        if (nationalId == null || nationalId.length() != EXPECTED_ID_LENGTH) {
            return nationalId;
        }

        try {
            return String.format("%s-%s-%s-%s-%s-%s",
                    nationalId.substring(0, 1),   // G
                    nationalId.substring(1, 5),   // YYYY
                    nationalId.substring(5, 6),   // #
                    nationalId.substring(6, 13),  // NNNNNNN
                    nationalId.substring(13, 14), // I
                    nationalId.substring(14, 16)  // CC
            );
        } catch (Exception e) {
            return nationalId;
        }
    }
}