# finGest

A personal finance management application built with Spring Boot and secured by Keycloak, designed to help users track their expenses, budgets, and savings.

## 📋 Features

- **User Authentication**: Secure login via Keycloak OAuth2/OpenID Connect
- **Expense Tracking**: Log and categorize your daily expenses
- **Budget Management**: Create and monitor budgets for different expense categories
- **Wallet Support**: Manage multiple accounts/wallets
- **Category Management**: Organize expenses with customizable categories

## 🛠️ Tech Stack

- **Backend**: Spring Boot 3.4.4 with Java 21
- **Security**: OAuth2 with Keycloak 26.2.3
- **Database**: PostgreSQL 16
- **Testing**: JUnit 5, JaCoCo for code coverage
- **Documentation**: OpenAPI 3 with SpringDoc
- **Container**: Docker & Docker Compose

## 🚀 Getting Started

### Prerequisites

- Java 21 or higher
- Docker and Docker Compose
- Maven

### Quick Start

1. Clone the repository
2. Start the required services using Docker Compose:

```bash
docker-compose up -d
```

This will start:
- PostgreSQL database for the application (port 5433)
- Keycloak server for authentication (port 8080)
- PostgreSQL database for Keycloak (port 5434)

3. Configure Keycloak following the instructions in [KEYCLOAK_SETUP.md](KEYCLOAK_SETUP.md)

4. Build and run the application:

```bash
./mvnw clean install
./mvnw spring-boot:run
```

The application will be available at `http://localhost:9090`

## 🔒 Authentication

The application uses Keycloak for authentication and authorization. A JWT token is required for accessing protected resources. See [KEYCLOAK_SETUP.md](KEYCLOAK_SETUP.md) for detailed setup instructions.

## 🧪 Testing

Run tests with:

```bash
# Run all tests
./mvnw test

# Generate test coverage report
./mvnw jacoco:report
```

Test coverage reports will be available at `target/site/jacoco/index.html`

## 📦 API Endpoints

- `/resources/categories` - Manage expense categories
- `/api/auth/userinfo` - Get authenticated user information
- `/api/auth/status` - Check authentication status

## 🐳 Docker Deployment

Build and run with Docker:

```bash
docker build -t fingest .
docker run -p 9090:9090 fingest
```

## 📄 License

This project is open source and available under the [license](LICENSE).

## 👥 Contributors

- [Borelli-7](https://kaly7.dev/) - Initial work