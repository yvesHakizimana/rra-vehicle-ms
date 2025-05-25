[![Java](https://img.shields.io/badge/Java-24-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Latest-blue.svg)](https://www.postgresql.org/)
[![Redis](https://img.shields.io/badge/Redis-Latest-red.svg)](https://redis.io/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

A comprehensive vehicle management system developed for the Rwanda Revenue Authority (RRA) to track vehicle registration, ownership transfers, plate number assignments, and complete vehicle history.

## 🚗 Overview

The RRA Vehicle Management System is designed to digitize and streamline vehicle tracking operations in Rwanda. The system manages the complete lifecycle of vehicles from initial registration through multiple ownership transfers, providing a transparent and auditable record of vehicle ownership history.

### Key Features

- **🔐 User Management**: Secure authentication with role-based access control (ADMIN/STANDARD)
- **👥 Vehicle Owner Management**: Registration and management of vehicle owners
- **🏷️ Plate Number System**: Assignment and tracking of vehicle plate numbers
- **🚙 Vehicle Registration**: Complete vehicle registration with chassis number validation
- **🔄 Ownership Transfers**: Seamless vehicle transfer between owners
- **📊 Vehicle History**: Complete ownership history tracking by chassis or plate number
- **🔍 Advanced Search**: Search capabilities across owners, vehicles, and transfers
- **📧 Email Notifications**: OTP-based account verification and password reset
- **🛡️ Security**: JWT-based authentication with rate limiting
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

### Security Features

- JWT-based authentication and authorization
- Role-based access control (RBAC)
- Rate limiting for authentication endpoints
- OTP verification for account activation
- Password reset with secure tokens
- Custom validation annotations

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

### Vehicle Management Endpoints

| Method | Endpoint | Description | Access |
|--------|----------|-------------|---------|
| `POST` | `/api/v1/vehicle-owners` | Register vehicle owner | ADMIN |
| `GET` | `/api/v1/vehicle-owners` | Get all owners (paginated) | ADMIN |
| `GET` | `/api/v1/vehicle-owners/{id}` | Get owner by ID | ADMIN |
| `GET` | `/api/v1/vehicle-owners/search` | Search owners | ADMIN |
| `POST` | `/api/v1/plate-numbers/owner/{ownerId}` | Register plate number | ADMIN |
| `GET` | `/api/v1/plate-numbers/owner/{ownerId}` | Get owner's plate numbers | USER |
| `POST` | `/api/v1/vehicles/owner/{ownerId}/plate-number/{plateId}` | Register vehicle | ADMIN |
| `GET` | `/api/v1/vehicles` | Get all vehicles | USER |
| `GET` | `/api/v1/vehicles/{id}` | Get vehicle by ID | USER |
| `GET` | `/api/v1/vehicles/owner/{ownerId}` | Get owner's vehicles | USER |

### Transfer & History Endpoints

| Method | Endpoint | Description | Access |
|--------|----------|-------------|---------|
| `POST` | `/api/v1/vehicle-transfers/vehicle/{vehicleId}/owner/{newOwnerId}/plate-number/{plateId}` | Transfer vehicle | ADMIN |
| `GET` | `/api/v1/vehicle-transfers/history/chassis/{chassisNumber}` | Get vehicle history by chassis | ADMIN |
| `GET` | `/api/v1/vehicle-transfers/history/plate/{plateNumber}` | Get vehicle history by plate | ADMIN |
| `GET` | `/api/v1/vehicle-transfers/history/owner/{ownerId}` | Get owner transfer history | ADMIN |
| `GET` | `/api/v1/vehicle-transfers` | Get all transfers | USER |

## 🏢 Business Logic

### Vehicle Registration Process

1. **Owner Registration**: Admin registers vehicle owner with personal details
2. **Plate Number Assignment**: Admin assigns plate number(s) to owner
3. **Vehicle Registration**: Admin registers vehicle linking owner and plate number
4. **Ownership Transfer**: Admin can transfer vehicle to new owner with new plate number

### Vehicle History Tracking

The system maintains complete ownership history:
- **Initial Registration**: Records first owner and purchase price
- **Ownership Transfers**: Tracks all subsequent owners with transfer amounts
- **Plate Number Changes**: Records plate number changes during transfers
- **Timeline View**: Chronological ownership history with ownership periods

### Security Model

- **Public Endpoints**: Registration, login, password reset
- **Authenticated Endpoints**: Vehicle viewing, owner information
- **Admin-Only Endpoints**: Registration operations, transfers, complete history access

## 🗂️ Project Structure

```
rra-vehicles-ms/
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
│   │   ├── generators/                # Custom ID generators
│   │   ├── validations/               # Custom validation annotations
│   │   └── GlobalExceptionHandler.java
│   ├── config/                        # Configuration classes
│   │   ├── OpenApiConfig.java        # Swagger configuration
│   │   ├── RedisConfig.java          # Redis configuration
│   │   └── ThymeleafConfig.java      # Email template configuration
│   ├── email/                         # Email service
│   ├── users/                         # User management
│   ├── vehicle_owners/                # Vehicle owner management
│   │   ├── dtos/                      # Owner DTOs
│   │   ├── mappers/                   # MapStruct mappers
│   │   ├── VehicleOwner.java         # Owner entity
│   │   ├── VehicleOwnerController.java
│   │   ├── VehicleOwnerService.java
│   │   └── VehicleOwnerRepository.java
│   ├── plate_numbers/                 # Plate number management
│   ├── vehicles/                      # Vehicle management
│   │   ├── dtos/                      # Vehicle DTOs
│   │   ├── mappers/                   # MapStruct mappers
│   │   ├── Vehicle.java              # Vehicle entity
│   │   ├── VehicleController.java
│   │   ├── VehicleService.java
│   │   └── VehicleRepository.java
│   ├── vehicle_history/               # Transfer & history management
│   │   ├── dtos/                      # Transfer DTOs
│   │   ├── mappers/                   # MapStruct mappers
│   │   ├── VehicleTransfer.java      # Transfer entity
│   │   ├── VehicleTransferController.java
│   │   ├── VehicleTransferService.java
│   │   └── repository/
│   └── RRAVehicleMsApplication.java   # Main application class
├── src/main/resources/
│   ├── templates/                     # Thymeleaf email templates
│   ├── application.properties         # Main configuration
│   └── application-dev.properties     # Development configuration
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

- [ ] Mobile API for vehicle registration
- [ ] Vehicle inspection management
- [ ] Integration with national ID system
- [ ] Real-time notifications
- [ ] Advanced analytics dashboard
- [ ] Bulk import/export functionality
- [ ] Multi-language support (Kinyarwanda, French, English)

### Version History

- **v1.0.0** - Initial release with core functionality
- **v1.1.0** - Enhanced security and validation (planned)
- **v2.0.0** - Mobile API and advanced features (planned)

---

**Rwanda Revenue Authority - Vehicle Management System**  
*Streamlining vehicle registration and ownership tracking in Rwanda*