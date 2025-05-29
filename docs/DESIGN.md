# Rwanda Government ERP System Design Documentation

## Table of Contents
- [Database ERD](#database-erd)
- [Application Architecture](#application-architecture)
- [List of POJOs](#list-of-pojos)
- [Technology Stack](#technology-stack)
- [Security Implementation](#security-implementation)
- [API Endpoints](#api-endpoints)

## Database ERD

The database schema consists of the following main entities:

### Employee
- **id**: UUID (Primary Key)
- **code**: String (Unique, auto-generated)
- **firstName**: String
- **lastName**: String
- **email**: String (Unique)
- **password**: String (Encrypted)
- **role**: Enum (ROLE_ADMIN, ROLE_MANAGER, ROLE_EMPLOYEE)
- **mobile**: String (Unique)
- **dateOfBirth**: Date
- **status**: Enum (ACTIVE, DISABLED)

### Employment
- **id**: UUID (Primary Key)
- **code**: String (Unique, auto-generated)
- **employeeId**: UUID (Foreign Key to Employee)
- **department**: String
- **position**: String
- **baseSalary**: BigDecimal
- **status**: Enum (ACTIVE, INACTIVE)
- **joiningDate**: Date

### Deduction
- **id**: UUID (Primary Key)
- **code**: String (Unique, auto-generated)
- **deductionName**: String (Unique)
- **percentage**: BigDecimal

### Payslip
- **id**: UUID (Primary Key)
- **employeeId**: UUID (Foreign Key to Employee)
- **houseAmount**: BigDecimal
- **transportAmount**: BigDecimal
- **employeeTaxedAmount**: BigDecimal
- **pensionAmount**: BigDecimal
- **medicalInsuranceAmount**: BigDecimal
- **otherTaxedAmount**: BigDecimal
- **grossSalary**: BigDecimal
- **netSalary**: BigDecimal
- **month**: Integer
- **year**: Integer
- **status**: Enum (PENDING, PAID)

### Message
- **id**: UUID (Primary Key)
- **employeeId**: UUID (Foreign Key to Employee)
- **message**: Text
- **month**: Integer
- **year**: Integer
- **createdAt**: Timestamp
- **sent**: Boolean

### Entity Relationships
- An **Employee** can have multiple **Employments** (One-to-Many)
- An **Employee** can have multiple **Payslips** (One-to-Many)
- An **Employee** can have multiple **Messages** (One-to-Many)

## Application Architecture

The application follows a layered architecture pattern:

1. **Presentation Layer**
   - REST Controllers
   - DTOs (Data Transfer Objects)
   - Request/Response handling

2. **Business Logic Layer**
   - Services
   - Business rules implementation
   - Transaction management

3. **Data Access Layer**
   - Repositories
   - Entity definitions
   - Database interactions

4. **Cross-Cutting Concerns**
   - Security
   - Validation
   - Exception handling
   - Logging

### Component Diagram

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│  REST API Layer │     │  Service Layer  │     │ Repository Layer│
│                 │     │                 │     │                 │
│  Controllers    │────▶│  Services       │────▶│  Repositories   │
│  DTOs           │◀────│  Business Logic │◀────│  Entities       │
└─────────────────┘     └─────────────────┘     └─────────────────┘
         ▲                       ▲                       ▲
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│  Security       │     │  Validation     │     │  Database       │
│                 │     │                 │     │                 │
│  JWT Auth       │     │  Bean Validation│     │  PostgreSQL     │
│  Role-based     │     │  Custom Rules   │     │  Flyway         │
└─────────────────┘     └─────────────────┘     └─────────────────┘
```

### Flow Diagram

1. Client sends HTTP request to REST endpoint
2. JWT Authentication filter validates token
3. Controller receives request and converts to DTO
4. Service layer processes business logic
5. Repository layer interacts with database
6. Response flows back through service and controller
7. Controller converts entity to DTO and returns response

## List of POJOs

### Entities
1. **Employee** - Represents an employee in the system
2. **Employment** - Represents an employment record for an employee
3. **Deduction** - Represents a deduction type with percentage
4. **Payslip** - Represents a monthly payslip for an employee
5. **Message** - Represents a notification message for an employee

### DTOs (Data Transfer Objects)
1. **EmployeeRequestDto** - For creating/updating employees
2. **EmployeeResponseDto** - For returning employee data
3. **EmploymentRequestDto** - For creating/updating employments
4. **EmploymentResponseDto** - For returning employment data
5. **DeductionRequestDto** - For creating/updating deductions
6. **DeductionResponseDto** - For returning deduction data
7. **PayslipRequestDto** - For creating/updating payslips
8. **PayslipResponseDto** - For returning payslip data
9. **MessageRequestDto** - For creating/updating messages
10. **MessageResponseDto** - For returning message data

### Enums
1. **Role** - Defines user roles (ROLE_ADMIN, ROLE_MANAGER, ROLE_EMPLOYEE)
2. **EmployeeStatus** - Defines employee status (ACTIVE, DISABLED)
3. **EmploymentStatus** - Defines employment status (ACTIVE, INACTIVE)
4. **PayslipStatus** - Defines payslip status (PENDING, PAID)

## Technology Stack

### Backend
- **Java 24** - Latest LTS version with modern language features
- **Spring Boot 3.4.5** - Framework for building production-ready applications
- **Spring Data JPA** - Simplifies data access layer implementation
- **Spring Security** - Provides authentication and authorization
- **Spring Validation** - For input validation
- **PostgreSQL** - Robust, open-source relational database
- **Flyway** - Database migration tool
- **JWT** - For stateless authentication
- **MapStruct** - For object mapping between entities and DTOs
- **Lombok** - Reduces boilerplate code
- **Swagger/OpenAPI** - API documentation

### DevOps & Infrastructure
- **Maven** - Build and dependency management
- **Redis** - For caching and session management
- **Resilience4j** - For circuit breaking and rate limiting
- **Spring Actuator** - For monitoring and metrics

## Security Implementation

The application implements a comprehensive security model:

### Authentication
- **JWT (JSON Web Token)** based authentication
- Token-based stateless authentication
- Access tokens (short-lived) and refresh tokens (long-lived)
- Secure password storage using BCrypt hashing

### Authorization
- **Role-based access control** with three roles:
  - **ROLE_ADMIN**: Can approve salary payments
  - **ROLE_MANAGER**: Can process salary and manage employee details
  - **ROLE_EMPLOYEE**: Can view personal details and payslips

### API Security
- All endpoints secured except for authentication endpoints
- CORS configuration to prevent cross-origin attacks
- Rate limiting to prevent brute force attacks
- Input validation to prevent injection attacks

### Security Flow
1. User authenticates with email/password
2. System validates credentials and issues JWT
3. JWT contains user identity and roles
4. Subsequent requests include JWT in Authorization header
5. System validates JWT signature and expiration
6. System authorizes access based on user role

## API Endpoints

### Authentication
- `POST /auth/login` - Authenticate user and get tokens
- `POST /auth/refresh` - Refresh access token
- `POST /auth/register` - Register new user
- `POST /auth/verify-account` - Verify user account
- `POST /auth/initiate-password-reset` - Initiate password reset
- `POST /auth/reset-password` - Reset password

### Employee Management
- `POST /api/v1/employees` - Create new employee
- `GET /api/v1/employees` - Get all employees
- `GET /api/v1/employees/{id}` - Get employee by ID
- `GET /api/v1/employees/email/{email}` - Get employee by email
- `GET /api/v1/employees/code/{code}` - Get employee by code
- `GET /api/v1/employees/status/{status}` - Get employees by status
- `PUT /api/v1/employees/{id}` - Update employee
- `DELETE /api/v1/employees/{id}` - Delete employee
- `PATCH /api/v1/employees/{id}/status` - Update employee status
- `PATCH /api/v1/employees/{id}/password` - Update employee password

### Employment Management
- `POST /api/v1/employments` - Create new employment
- `GET /api/v1/employments` - Get all employments
- `GET /api/v1/employments/{id}` - Get employment by ID
- `GET /api/v1/employments/code/{code}` - Get employment by code
- `GET /api/v1/employments/employee/{employeeId}` - Get employments by employee
- `GET /api/v1/employments/status/{status}` - Get employments by status
- `GET /api/v1/employments/employee/{employeeId}/current` - Get current employment
- `PUT /api/v1/employments/{id}` - Update employment
- `DELETE /api/v1/employments/{id}` - Delete employment
- `PATCH /api/v1/employments/{id}/status` - Update employment status

### Deduction Management
- `POST /api/v1/deductions` - Create new deduction
- `GET /api/v1/deductions` - Get all deductions
- `GET /api/v1/deductions/{id}` - Get deduction by ID
- `GET /api/v1/deductions/code/{code}` - Get deduction by code
- `GET /api/v1/deductions/name/{name}` - Get deduction by name
- `PUT /api/v1/deductions/{id}` - Update deduction
- `DELETE /api/v1/deductions/{id}` - Delete deduction
- `POST /api/v1/deductions/initialize` - Initialize default deductions

### Payroll Management
- `POST /api/v1/payroll/process/month/{month}/year/{year}` - Process payroll for month/year
- `POST /api/v1/payroll/approve/month/{month}/year/{year}` - Approve payroll for month/year
- `GET /api/v1/payslips/{id}` - Get payslip by ID
- `GET /api/v1/payslips/employee/{employeeId}` - Get payslips by employee
- `GET /api/v1/payslips/employee/{employeeId}/status/{status}` - Get payslips by employee and status
- `GET /api/v1/payslips/status/{status}` - Get payslips by status
- `GET /api/v1/payslips/month/{month}/year/{year}` - Get payslips by month/year
- `GET /api/v1/payslips/month/{month}/year/{year}/status/{status}` - Get payslips by month/year/status
- `GET /api/v1/payslip/employee/{employeeId}/month/{month}/year/{year}` - Get specific payslip
- `PATCH /api/v1/payslips/{id}/status` - Update payslip status

### Message Management
- `GET /api/v1/messages/{id}` - Get message by ID
- `GET /api/v1/messages/employee/{employeeId}` - Get messages by employee
- `GET /api/v1/messages/employee/{employeeId}/month/{month}/year/{year}` - Get messages by employee/month/year
- `GET /api/v1/messages/month/{month}/year/{year}` - Get messages by month/year
- `GET /api/v1/messages/unsent` - Get unsent messages
- `POST /api/v1/messages/{id}/send` - Send specific message
- `POST /api/v1/messages/send-all-unsent` - Send all unsent messages