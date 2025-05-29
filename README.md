[![Java](https://img.shields.io/badge/Java-24-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Latest-blue.svg)](https://www.postgresql.org/)
[![Redis](https://img.shields.io/badge/Redis-Latest-red.svg)](https://redis.io/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

A comprehensive Enterprise Resource Planning (ERP) system for the Government of Rwanda, focusing on Employee Management and Payroll Processing with updated pension rates and deduction calculations.

## 💼 Overview

The Rwanda Government ERP System is designed to modernize and streamline employee management and payroll processing for government institutions. The system implements the updated pension rates (increased from 3% to 6%) and manages the complete payroll process from employee registration to salary payment notifications.

### Key Features

- **👤 Employee Management**: Store and manage employee personal and professional information
- **💼 Employment Tracking**: Track employee positions, departments, and salary information
- **💰 Payroll Processing**: Calculate salaries with appropriate deductions and taxes
- **🧾 Payslip Generation**: Generate monthly payslips for employees
- **📱 Notification System**: Send salary payment notifications to employees
- **🔐 Role-Based Access Control**: Different permissions for Admins, Managers, and Employees
- **📊 Deduction Management**: Configurable deduction rates for taxes, pension, etc.
- **📧 Email Notifications**: Automated salary payment notifications
- **🛡️ Security**: JWT-based authentication with role-based authorization
- **📖 API Documentation**: Comprehensive Swagger/OpenAPI documentation

## 🏗️ Architecture

### Technology Stack

- **Backend Framework**: Spring Boot 3.4.5
- **Java Version**: Java 24
- **Database**: PostgreSQL
- **Caching**: Redis
- **Authentication**: JWT (JSON Web Tokens)
- **API Documentation**: SpringDoc OpenAPI 3 (Swagger)
- **Email Service**: Spring Mail with Thymeleaf templates
- **Rate Limiting**: Resilience4j
- **Build Tool**: Maven
- **ORM**: Hibernate/JPA
- **Mapping**: MapStruct
- **Validation**: Jakarta Bean Validation
- **Database Migrations**: Flyway

### Security Features

- JWT-based authentication and authorization
- Role-based access control (RBAC) with three roles:
  - **ROLE_ADMIN**: Can approve salary payments
  - **ROLE_MANAGER**: Can process salary and manage employee details
  - **ROLE_EMPLOYEE**: Can view personal details and payslips
- Rate limiting for authentication endpoints
- Password encryption with BCrypt
- Secure email notifications
- Input validation to prevent injection attacks

## 📋 Prerequisites

- **Java 24** or higher
- **Maven 3.8+**
- **PostgreSQL 12+**
- **Redis 6+**
- **SMTP Server** (for email functionality)

## 🚀 Quick Start

### 1. Clone the Repository

```bash
git clone https://github.com/your-org/rra-vehicle-ms.git
cd rra-vehicle-ms
```

### 2. Database Setup

```sql
-- Create PostgreSQL database
CREATE DATABASE rra_ms;
CREATE USER rra_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE rra_ms TO rra_user;
```

### 3. Redis Setup

```bash
# Start Redis server
redis-server

# Or using Docker
docker run -d -p 6379:6379 --name redis redis:latest
```

### 4. Environment Configuration

Create application-dev.properties:

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/rra_ms
spring.datasource.username=rra_user
spring.datasource.password=your_password

# Redis Configuration
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.password=your_redis_password

# JWT Configuration
spring.jwt.secret=your_jwt_secret_key_here
spring.jwt.access-token-expiration=900
spring.jwt.refresh-token-expiration=604800

# Email Configuration
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

### 5. Run the Application

```bash
# Using Maven
./mvnw spring-boot:run

# Or compile and run
./mvnw clean package
java -jar target/rra-ms-0.0.1-SNAPSHOT.jar
```

### 6. Access the Application

- **API Base URL**: `http://localhost:8080`
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **Health Check**: `http://localhost:8080/actuator/health`

## 📚 API Documentation

### Authentication Endpoints

| Method | Endpoint | Description | Public |
|--------|----------|-------------|---------|
| `POST` | `/auth/register` | Register new user | ✅ |
| `POST` | `/auth/login` | User login | ✅ |
| `PATCH` | `/auth/verify-account` | Verify account with OTP | ✅ |
| `POST` | `/auth/initiate-password-reset` | Request password reset | ✅ |
| `PATCH` | `/auth/reset-password` | Reset password with OTP | ✅ |
| `POST` | `/auth/refresh` | Refresh access token | ✅ |

### Employee Management Endpoints

| Method | Endpoint | Description | Access |
|--------|----------|-------------|---------|
| `POST` | `/api/v1/employees` | Create new employee | MANAGER |
| `GET` | `/api/v1/employees` | Get all employees | ADMIN, MANAGER |
| `GET` | `/api/v1/employees/{id}` | Get employee by ID | ADMIN, MANAGER |
| `GET` | `/api/v1/employees/email/{email}` | Get employee by email | ADMIN, MANAGER |
| `GET` | `/api/v1/employees/code/{code}` | Get employee by code | ADMIN, MANAGER |
| `GET` | `/api/v1/employees/status/{status}` | Get employees by status | ADMIN, MANAGER |
| `PUT` | `/api/v1/employees/{id}` | Update employee | MANAGER |
| `DELETE` | `/api/v1/employees/{id}` | Delete employee | MANAGER |
| `PATCH` | `/api/v1/employees/{id}/status` | Update employee status | MANAGER |
| `PATCH` | `/api/v1/employees/{id}/password` | Update employee password | MANAGER |

### Employment Management Endpoints

| Method | Endpoint | Description | Access |
|--------|----------|-------------|---------|
| `POST` | `/api/v1/employments` | Create new employment | MANAGER |
| `GET` | `/api/v1/employments` | Get all employments | ADMIN, MANAGER |
| `GET` | `/api/v1/employments/{id}` | Get employment by ID | ADMIN, MANAGER |
| `GET` | `/api/v1/employments/code/{code}` | Get employment by code | ADMIN, MANAGER |
| `GET` | `/api/v1/employments/employee/{employeeId}` | Get employments by employee | ADMIN, MANAGER, EMPLOYEE |
| `GET` | `/api/v1/employments/status/{status}` | Get employments by status | ADMIN, MANAGER |
| `GET` | `/api/v1/employments/employee/{employeeId}/current` | Get current employment | ADMIN, MANAGER, EMPLOYEE |
| `PUT` | `/api/v1/employments/{id}` | Update employment | MANAGER |
| `DELETE` | `/api/v1/employments/{id}` | Delete employment | MANAGER |
| `PATCH` | `/api/v1/employments/{id}/status` | Update employment status | MANAGER |

### Deduction & Payroll Management Endpoints

| Method | Endpoint | Description | Access |
|--------|----------|-------------|---------|
| `POST` | `/api/v1/deductions` | Create new deduction | MANAGER |
| `GET` | `/api/v1/deductions` | Get all deductions | ALL |
| `GET` | `/api/v1/deductions/{id}` | Get deduction by ID | ADMIN, MANAGER |
| `PUT` | `/api/v1/deductions/{id}` | Update deduction | MANAGER |
| `DELETE` | `/api/v1/deductions/{id}` | Delete deduction | MANAGER |
| `POST` | `/api/v1/deductions/initialize` | Initialize default deductions | MANAGER |
| `POST` | `/api/v1/payroll/process/month/{month}/year/{year}` | Process payroll | MANAGER |
| `POST` | `/api/v1/payroll/approve/month/{month}/year/{year}` | Approve payroll | ADMIN |
| `GET` | `/api/v1/payslips/employee/{employeeId}` | Get employee payslips | ALL |
| `GET` | `/api/v1/payslip/employee/{employeeId}/month/{month}/year/{year}` | Get specific payslip | ALL |

## 🏢 Business Logic

### Employee Management Process

1. **Employee Registration**: Manager registers employee with personal and professional details
2. **Employment Assignment**: Manager creates employment record with department, position, and salary
3. **Deduction Configuration**: System applies standard deductions or custom configurations
4. **Payroll Processing**: Manager processes payroll for a specific month and year
5. **Payroll Approval**: Admin approves processed payroll for payment
6. **Notification**: System sends payment notifications to employees

### Payroll Calculation

The system calculates employee compensation based on the following formula:

1. **Gross Salary Calculation**:
   ```
   Gross Salary = Base Salary + Housing Allowance + Transport Allowance
   Housing Allowance = Base Salary * 14%
   Transport Allowance = Base Salary * 14%
   ```

2. **Deductions Calculation**:
   ```
   Employee Tax = Base Salary * 30%
   Pension = Base Salary * 6%
   Medical Insurance = Base Salary * 5%
   Other Deductions = Base Salary * 5%
   ```

3. **Net Salary Calculation**:
   ```
   Net Salary = Gross Salary - (Employee Tax + Pension + Medical Insurance + Other Deductions)
   ```

### Security Model

- **Public Endpoints**: Registration, login, password reset
- **Employee Access**: View personal details, employment history, and payslips
- **Manager Access**: Employee management, employment records, payroll processing
- **Admin Access**: Payroll approval, system configuration

## 🗂️ Project Structure

```
rw-gov-erp/
├── src/main/java/com/ne/rra_vehicle_ms/
│   ├── auth/                          # Authentication & security
│   │   ├── dtos/                      # Authentication DTOs
│   │   ├── AuthController.java        # Auth endpoints
│   │   ├── JwtService.java           # JWT token management
│   │   ├── OtpService.java           # OTP generation/validation
│   │   └── SecurityConfig.java       # Security configuration
│   ├── commons/                       # Shared components
│   │   ├── dtos/                      # Common DTOs (ApiResponse, PageResponse)
│   │   ├── exceptions/                # Custom exceptions
│   │   ├── generators/                # Custom ID generators (Base36Generator)
│   │   ├── validations/               # Custom validation annotations
│   │   └── GlobalExceptionHandler.java
│   ├── config/                        # Configuration classes
│   │   ├── OpenApiConfig.java        # Swagger configuration
│   │   ├── RedisConfig.java          # Redis configuration
│   │   ├── EmailConfig.java          # Email configuration
│   │   └── ThymeleafConfig.java      # Email template configuration
│   ├── email/                         # Email service
│   ├── users/                         # User management
│   ├── employee/                      # Employee management
│   │   ├── controllers/               # Employee controllers
│   │   ├── dtos/                      # Employee DTOs
│   │   ├── entities/                  # Employee entities
│   │   ├── mappers/                   # MapStruct mappers
│   │   ├── repositories/              # Employee repositories
│   │   └── services/                  # Employee services
│   ├── employment/                    # Employment management
│   │   ├── controllers/               # Employment controllers
│   │   ├── dtos/                      # Employment DTOs
│   │   ├── entities/                  # Employment entities
│   │   ├── mappers/                   # MapStruct mappers
│   │   ├── repositories/              # Employment repositories
│   │   └── services/                  # Employment services
│   ├── deductions/                    # Deduction management
│   │   ├── controllers/               # Deduction controllers
│   │   ├── dtos/                      # Deduction DTOs
│   │   ├── entities/                  # Deduction entities
│   │   ├── mappers/                   # MapStruct mappers
│   │   ├── repositories/              # Deduction repositories
│   │   └── services/                  # Deduction services
│   ├── payslip/                       # Payslip management
│   │   ├── controllers/               # Payslip controllers
│   │   ├── dtos/                      # Payslip DTOs
│   │   ├── entities/                  # Payslip entities
│   │   ├── mappers/                   # MapStruct mappers
│   │   ├── repositories/              # Payslip repositories
│   │   └── services/                  # Payslip services
│   ├── message/                       # Message management
│   │   ├── controllers/               # Message controllers
│   │   ├── dtos/                      # Message DTOs
│   │   ├── entities/                  # Message entities
│   │   ├── mappers/                   # MapStruct mappers
│   │   ├── repositories/              # Message repositories
│   │   └── services/                  # Message services
│   └── RwGovErpApplication.java       # Main application class
├── src/main/resources/
│   ├── db/migration/                  # Flyway database migrations
│   ├── templates/                     # Thymeleaf email templates
│   ├── application.properties         # Main configuration
│   └── application-dev.properties     # Development configuration
├── docs/                              # Documentation
│   ├── DESIGN.md                      # Design documentation
│   └── ERD.md                         # Entity Relationship Diagram
├── src/test/                          # Test classes
├── pom.xml                           # Maven configuration
└── README.md                         # This file
```

## 🧪 Testing

### Running Tests

```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=VehicleServiceTest

# Run tests with coverage
./mvnw test jacoco:report
```

### Test Categories

- **Unit Tests**: Service layer and utility classes
- **Integration Tests**: Repository layer with H2 database
- **Controller Tests**: Web layer with MockMvc
- **Security Tests**: Authentication and authorization

## 🔧 Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_PROFILES_ACTIVE` | Active Spring profile | `dev` |
| `JWT_SECRET` | JWT signing secret | - |
| `DB_URL` | PostgreSQL connection URL | `jdbc:postgresql://localhost:5432/rra_ms` |
| `DB_USERNAME` | Database username | `postgres` |
| `DB_PASSWORD` | Database password | - |
| `REDIS_HOST` | Redis server host | `localhost` |
| `REDIS_PORT` | Redis server port | `6379` |
| `REDIS_PASSWORD` | Redis password | - |
| `MAIL_HOST` | SMTP server host | - |
| `MAIL_USERNAME` | Email username | - |
| `MAIL_PASSWORD` | Email password | - |

### Rate Limiting Configuration

```properties
# Authentication rate limiting
resilience4j.ratelimiter.instances.auth-rate-limiter.limit-for-period=10
resilience4j.ratelimiter.instances.auth-rate-limiter.limit-refresh-period=30s

# OTP rate limiting
resilience4j.ratelimiter.instances.otp-rate-limiter.limit-for-period=2
resilience4j.ratelimiter.instances.otp-rate-limiter.limit-refresh-period=10m
```

## 📊 Monitoring & Health Checks

### Available Endpoints

- **Health Check**: `/actuator/health`
- **Application Info**: `/actuator/info`
- **Metrics**: `/actuator/prometheus` (if Prometheus is configured)

### Logging Configuration

```properties
# Security debugging
logging.level.org.springframework.security=DEBUG

# SQL query logging
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.orm.jdbc.bind=TRACE

# JWT debugging
logging.level.com.ne.rra_vehicle_ms.auth.JwtService=DEBUG
```

## 🐳 Docker Deployment

### Docker Compose Setup

```yaml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - DB_URL=jdbc:postgresql://db:5432/rra_ms
      - REDIS_HOST=redis
    depends_on:
      - db
      - redis

  db:
    image: postgres:15
    environment:
      POSTGRES_DB: rra_ms
      POSTGRES_USER: rra_user
      POSTGRES_PASSWORD: your_password
    volumes:
      - postgres_data:/var/lib/postgresql/data

  redis:
    image: redis:7-alpine
    command: redis-server --requirepass your_redis_password

volumes:
  postgres_data:
```

### Dockerfile

```dockerfile
FROM openjdk:24-jre-slim

WORKDIR /app

COPY target/rra-ms-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

## 🤝 Contributing

### Development Setup

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/new-feature`
3. Make your changes and add tests
4. Ensure all tests pass: mvnw test`
5. Commit your changes: `git commit -am 'Add new feature'`
6. Push to the branch: `git push origin feature/new-feature`
7. Submit a pull request

### Code Style

- Follow Java naming conventions
- Use meaningful variable and method names
- Add JavaDoc comments for public methods
- Maintain test coverage above 80%
- Use consistent formatting (Spring Boot style)

### Commit Message Format

```
type(scope): description

[optional body]

[optional footer]
```

Types: `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`

## 📝 API Examples

### Register Vehicle Owner

```bash
curl -X POST http://localhost:8080/api/v1/vehicle-owners \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "firstName": "Jean",
    "lastName": "Uwimana",
    "email": "jean.uwimana@example.com",
    "phoneNumber": "0788123456",
    "nationalId": "1198570000123401",
    "addressDto": {
      "district": "Kigali",
      "sector": "Nyarugenge"
    }
  }'
```

### Register Vehicle

```bash
curl -X POST http://localhost:8080/api/v1/vehicles/owner/{ownerId}/plate-number/{plateNumberId} \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "chasisNumber": "1HGBH41JXMN109186",
    "manufacturer": "Toyota",
    "modelName": "Camry",
    "yearOfManufacture": 2020,
    "price": 15000000
  }'
```

### Get Vehicle History

```bash
curl -X GET http://localhost:8080/api/v1/vehicle-transfers/history/chassis/1HGBH41JXMN109186 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## 🚨 Troubleshooting

### Common Issues

1. **Database Connection Failed**
    - Verify PostgreSQL is running
    - Check connection string and credentials
    - Ensure database exists

2. **Redis Connection Failed**
    - Verify Redis server is running
    - Check Redis host and port configuration
    - Verify Redis password if authentication is enabled

3. **JWT Token Issues**
    - Ensure JWT secret is properly configured
    - Check token expiration settings
    - Verify token format in Authorization header

4. **Email Service Issues**
    - Verify SMTP configuration
    - Check email credentials
    - Ensure firewall allows SMTP connections

### Debug Mode

Enable debug logging:

```properties
logging.level.com.ne.rra_vehicle_ms=DEBUG
logging.level.org.springframework.security=DEBUG
```

## 📄 License

This project is licensed under the Apache License 2.0 - see the LICENSE file for details.

## 👥 Authors

- **Yves HAKIZIMANA** - [yvhakizimana123@gmail.com](mailto:yvhakizimana123@gmail.com)

## 📞 Support

For support and questions:

- **Email**: yvhakizimana123@gmail.com
- **Issue Tracker**: [GitHub Issues](https://github.com/your-org/rra-vehicle-ms/issues)

## 🗺️ Roadmap

### Upcoming Features

- [ ] Mobile application for employees to view payslips
- [ ] Advanced reporting and analytics dashboard
- [ ] Integration with banking systems for direct deposits
- [ ] Tax filing automation
- [ ] Performance management module
- [ ] Leave management integration
- [ ] Multi-language support (Kinyarwanda, French, English)

### Version History

- **v1.0.0** - Initial release with core functionality
- **v1.1.0** - Enhanced reporting and analytics (planned)
- **v2.0.0** - Mobile application and banking integration (planned)

---

**Rwanda Government - Enterprise Resource Planning System**  
*Modernizing employee management and payroll processing for Rwanda's government institutions*
