
-- Create new tables for ERP system
CREATE TABLE employees
(
    id           UUID         NOT NULL,
    code         VARCHAR(7)   NOT NULL,
    first_name   VARCHAR(255) NOT NULL,
    last_name    VARCHAR(255) NOT NULL,
    email        VARCHAR(255) NOT NULL,
    password     VARCHAR(255) NOT NULL,
    role         VARCHAR(255) NOT NULL,
    mobile       VARCHAR(255) NOT NULL,
    date_of_birth DATE        NOT NULL,
    status       VARCHAR(255) NOT NULL,
    CONSTRAINT pk_employees PRIMARY KEY (id)
);

CREATE TABLE employments
(
    id           UUID         NOT NULL,
    code         VARCHAR(7)   NOT NULL,
    employee_id  UUID         NOT NULL,
    department   VARCHAR(255) NOT NULL,
    position     VARCHAR(255) NOT NULL,
    base_salary  DECIMAL(19, 2) NOT NULL,
    status       VARCHAR(255) NOT NULL,
    joining_date DATE         NOT NULL,
    CONSTRAINT pk_employments PRIMARY KEY (id)
);

CREATE TABLE deductions
(
    id             UUID         NOT NULL,
    code           VARCHAR(7)   NOT NULL,
    deduction_name VARCHAR(255) NOT NULL,
    percentage     DECIMAL(5, 2) NOT NULL,
    CONSTRAINT pk_deductions PRIMARY KEY (id)
);

CREATE TABLE payslips
(
    id                       UUID         NOT NULL,
    employee_id              UUID         NOT NULL,
    house_amount             DECIMAL(19, 2) NOT NULL,
    transport_amount         DECIMAL(19, 2) NOT NULL,
    employee_taxed_amount    DECIMAL(19, 2) NOT NULL,
    pension_amount           DECIMAL(19, 2) NOT NULL,
    medical_insurance_amount DECIMAL(19, 2) NOT NULL,
    other_taxed_amount       DECIMAL(19, 2) NOT NULL,
    gross_salary             DECIMAL(19, 2) NOT NULL,
    net_salary               DECIMAL(19, 2) NOT NULL,
    month                    INTEGER      NOT NULL,
    year                     INTEGER      NOT NULL,
    status                   VARCHAR(255) NOT NULL,
    CONSTRAINT pk_payslips PRIMARY KEY (id)
);

CREATE TABLE messages
(
    id           UUID         NOT NULL,
    employee_id  UUID         NOT NULL,
    message      TEXT         NOT NULL,
    month        INTEGER      NOT NULL,
    year         INTEGER      NOT NULL,
    created_at   TIMESTAMP    NOT NULL,
    sent         BOOLEAN      NOT NULL,
    CONSTRAINT pk_messages PRIMARY KEY (id)
);

-- Create unique indexes
CREATE UNIQUE INDEX idx_employee_email_unq ON employees (email);
CREATE UNIQUE INDEX idx_employee_mobile_unq ON employees (mobile);
CREATE UNIQUE INDEX idx_employee_code_unq ON employees (code);

CREATE UNIQUE INDEX idx_employment_code_unq ON employments (code);
CREATE INDEX idx_employment_employee_id ON employments (employee_id);

CREATE UNIQUE INDEX idx_deduction_code_unq ON deductions (code);
CREATE UNIQUE INDEX idx_deduction_name_unq ON deductions (deduction_name);

CREATE UNIQUE INDEX idx_payslip_employee_month_year ON payslips (employee_id, month, year);

-- Add foreign key constraints
ALTER TABLE employments
    ADD CONSTRAINT fk_employments_on_employee FOREIGN KEY (employee_id) REFERENCES employees (id);

ALTER TABLE payslips
    ADD CONSTRAINT fk_payslips_on_employee FOREIGN KEY (employee_id) REFERENCES employees (id);

ALTER TABLE messages
    ADD CONSTRAINT fk_messages_on_employee FOREIGN KEY (employee_id) REFERENCES employees (id);

-- Create trigger function for payslip approval
CREATE OR REPLACE FUNCTION create_payslip_approval_message()
RETURNS TRIGGER AS $$
DECLARE
employee_first_name VARCHAR(255);
    month_name VARCHAR(20);
    institution_name VARCHAR(255) := 'Rwanda Government';
    message_text TEXT;
BEGIN
    -- Only proceed if status is changed to 'PAID'
    IF NEW.status = 'PAID' AND (OLD.status IS NULL OR OLD.status <> 'PAID') THEN
        -- Get employee first name
SELECT first_name INTO employee_first_name
FROM employees
WHERE id = NEW.employee_id;

-- Get month name
CASE NEW.month
            WHEN 1 THEN month_name := 'JANUARY';
WHEN 2 THEN month_name := 'FEBRUARY';
WHEN 3 THEN month_name := 'MARCH';
WHEN 4 THEN month_name := 'APRIL';
WHEN 5 THEN month_name := 'MAY';
WHEN 6 THEN month_name := 'JUNE';
WHEN 7 THEN month_name := 'JULY';
WHEN 8 THEN month_name := 'AUGUST';
WHEN 9 THEN month_name := 'SEPTEMBER';
WHEN 10 THEN month_name := 'OCTOBER';
WHEN 11 THEN month_name := 'NOVEMBER';
WHEN 12 THEN month_name := 'DECEMBER';
ELSE month_name := 'UNKNOWN';
END CASE;

        -- Create message text
        message_text := 'Dear ' || employee_first_name || ', your salary for ' || month_name || '/' || NEW.year ||
                        ' from ' || institution_name || ' amounting to ' || NEW.net_salary ||
                        ' has been credited to your account successfully.';

        -- Insert message
INSERT INTO messages (id, employee_id, message, month, year, created_at, sent)
VALUES (gen_random_uuid(), NEW.employee_id, message_text, NEW.month, NEW.year, NOW(), FALSE);
END IF;

RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create trigger
CREATE TRIGGER payslip_approval_trigger
    AFTER INSERT OR UPDATE ON payslips
                        FOR EACH ROW
                        EXECUTE FUNCTION create_payslip_approval_message();