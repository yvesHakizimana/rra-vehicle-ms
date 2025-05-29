# Rwanda Government ERP System - Entity Relationship Diagram

```mermaid
erDiagram
    Employee ||--o{ Employment : "has"
    Employee ||--o{ Payslip : "receives"
    Employee ||--o{ Message : "receives"

    Employee {
        UUID id PK
        String code UK
        String firstName
        String lastName
        String email UK
        String password
        Role role
        String mobile UK
        Date dateOfBirth
        EmployeeStatus status
    }

    Employment {
        UUID id PK
        String code UK
        UUID employeeId FK
        String department
        String position
        BigDecimal baseSalary
        EmploymentStatus status
        Date joiningDate
    }

    Deduction {
        UUID id PK
        String code UK
        String deductionName UK
        BigDecimal percentage
    }

    Payslip {
        UUID id PK
        UUID employeeId FK
        BigDecimal houseAmount
        BigDecimal transportAmount
        BigDecimal employeeTaxedAmount
        BigDecimal pensionAmount
        BigDecimal medicalInsuranceAmount
        BigDecimal otherTaxedAmount
        BigDecimal grossSalary
        BigDecimal netSalary
        Integer month
        Integer year
        PayslipStatus status
    }

    Message {
        UUID id PK
        UUID employeeId FK
        Text message
        Integer month
        Integer year
        Timestamp createdAt
        Boolean sent
    }
```

## Legend
- PK: Primary Key
- FK: Foreign Key
- UK: Unique Key

## Relationships
1. One **Employee** can have multiple **Employments** (One-to-Many)
2. One **Employee** can have multiple **Payslips** (One-to-Many)
3. One **Employee** can have multiple **Messages** (One-to-Many)

## Constraints
- Employee email, mobile, and code must be unique
- Employment code must be unique
- Deduction code and name must be unique
- Payslip must be unique for an employee, month, and year combination