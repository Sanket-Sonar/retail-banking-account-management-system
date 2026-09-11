# Retail Banking Account Management System

## Table of Contents
- Project Overview
- Business Objectives
- Features
- Technology Stack
- Architecture
- Module Details
- Database Design
- Security Implementation
- API Endpoints
- Installation Guide
- Configuration
- Running the Application
- Testing
- Code Coverage
- Logging
- Exception Handling
- Future Enhancements
- Author

## Project Overview

The Retail Banking Account Management System is a Spring Boot based REST API application designed to manage customer banking operations securely and efficiently. The system supports customer onboarding, account management, fund transactions, fixed deposits, transaction history, and role-based access control.

The project follows a layered architecture using Spring Boot, Spring Security, JPA/Hibernate, JWT authentication, and MySQL.

## Business Objectives

- Manage customer accounts securely.
- Enable account balance inquiry.
- Support credit and debit transactions.
- Maintain transaction history.
- Manage Fixed Deposits (FDs).
- Provide role-based access for customers and administrators.
- Ensure secure authentication and authorization.

## Features

### Authentication & Authorization
- User Registration
- User Login
- JWT Token Generation
- Password Encryption using BCrypt
- Role-Based Authorization
- CUSTOMER and ADMIN roles

### Account Management
- Create Account
- View Account Details
- Get Account Balance
- Update Account Information
- Account Ownership Validation

### Transaction Management
- Credit Amount
- Debit Amount
- View Last 10 Transactions
- View Complete Transaction History
- Insufficient Balance Validation

### Fixed Deposit Management
- Create Fixed Deposit
- Calculate FD Maturity Amount
- View FD Details
- View Customer FDs

### Admin Features
- View All Customers
- View All Accounts
- View All Transactions
- Monitor Banking Activities

## Technology Stack

### Backend
- Java 21
- Spring Boot 3.x
- Spring MVC
- Spring Security
- Spring Data JPA
- Hibernate ORM

### Database
- MySQL 8+

### Documentation
- Swagger/OpenAPI

### Build Tool
- Maven

### Testing
- JUnit 5
- Mockito
- MockMvc
- JaCoCo

### Utilities
- Lombok
- Model Mapper / Custom Mapper

## Architecture

```text
Controller Layer
        |
Service Layer
        |
Repository Layer
        |
Database
```

### Layer Responsibilities

#### Controller Layer
Handles incoming HTTP requests and returns API responses.

#### Service Layer
Contains business logic and validations.

#### Repository Layer
Performs database operations using Spring Data JPA.

#### Database Layer
Stores users, accounts, transactions, and FD information.

## Database Design

### USER

| Field | Type |
|---------|---------|
| id | Long |
| fullName | String |
| email | String |
| password | String |
| role | Enum |

### ACCOUNT

| Field | Type |
|---------|---------|
| id | Long |
| accountNumber | String |
| accountType | String |
| currentBalance | Double |
| branchName | String |
| openingDate | LocalDate |

### TRANSACTION

| Field | Type |
|---------|---------|
| id | Long |
| transactionId | String |
| amount | Double |
| transactionType | String |
| transactionDate | LocalDateTime |

### FIXED_DEPOSIT

| Field | Type |
|---------|---------|
| id | Long |
| fdNumber | String |
| depositAmount | Double |
| tenureYears | Integer |
| interestRate | Double |
| maturityAmount | Double |

## Security Implementation

### Authentication Flow
1. User registers.
2. User logs in.
3. JWT token generated.
4. Token included in request header.
5. Security filter validates token.
6. Authorized user accesses APIs.

### Authorization Rules

| Role | Access |
|--------|--------|
| CUSTOMER | Own Account Data |
| ADMIN | All Accounts and Transactions |

## Sample API Endpoints

### Authentication APIs

```http
POST /api/auth/register
POST /api/auth/login
```

### Account APIs

```http
POST /api/accounts
GET /api/accounts/{accountNumber}
GET /api/accounts/{accountNumber}/balance
PUT /api/accounts/{accountNumber}
```

### Transaction APIs

```http
POST /api/accounts/{accountNumber}/credit
POST /api/accounts/{accountNumber}/debit
```

### Fixed Deposit APIs

```http
POST /api/fd
GET  /api/fd/{fdNumber}
GET  /api/fd/customer
```

## Installation Guide

### Clone Repository

```bash
git clone https://github.com/your-org/retail-banking-system.git
```

### Configure Database

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/banking_db
spring.datasource.username=root
spring.datasource.password=root
spring.jpa.hibernate.ddl-auto=update
```

### Build Project

```bash
mvn clean install
```

### Start Application

```bash
mvn spring-boot:run
```

Server URL

```text
http://localhost:8080
```

## API Documentation

### Swagger UI

```text
http://localhost:8080/swagger-ui/index.html
```

### OpenAPI JSON

```text
http://localhost:8080/v3/api-docs
```

## Testing

Run all test cases:

```bash
mvn test
```

Run with coverage:

```bash
mvn clean verify
```

## JaCoCo Coverage

Coverage Report Location:

```text
target/site/jacoco/index.html
```

Target Coverage:

- Controller Layer: 90%+
- Service Layer: 90%+
- Overall Project Coverage: 90%+

## Logging

Implemented using:

- SLF4J
- Logback

Log File:

```text
logs/application.log
```

## Exception Handling

Global Exception Handler manages:

- ResourceNotFoundException
- AccountNotFoundException
- FixedDepositNotFoundException
- InvalidRequestException
- UnauthorizedAccessException
- InsufficientBalanceException
- ValidationException

## Project Standards

- SOLID Principles
- Clean Code Practices
- REST API Standards
- Layered Architecture
- DTO Pattern
- Mapper Pattern
- Global Exception Handling
- JWT Security
- Unit Testing
- Code Coverage > 90%

## Author

**Sanket Sonar**

Consultant | Java Spring Boot Developer

Pune, Maharashtra, India
