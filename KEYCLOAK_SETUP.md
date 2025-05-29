# Keycloak Configuration for FinGest

This document provides instructions on how to set up Keycloak for authentication and authorization in the FinGest application.

## 1. Running Keycloak with Docker Compose

The application includes a Docker Compose configuration file for running Keycloak:

```bash
# Start Keycloak and dependent services
docker-compose up -d
```

This will start:
- PostgreSQL databases for the application and Keycloak
- Keycloak server on port 8080

## 2. Configuring Keycloak

After Keycloak has started, complete the following manual setup steps:

1. Access the Keycloak admin console at http://localhost:8080/admin
   - Login with username: `admin` and password: `admin`

2. Create a new realm:
   - Click "Create Realm" 
   - Enter "fingest-realm" as the Name
   - Click "Create"

3. Create a client:
   - Go to "Clients" → "Create Client"
   - Enter "fingest-client" as the Client ID
   - Enable "Client authentication"
   - Set "Authentication flow" to "Standard flow"
   - Set "Valid redirect URIs" to "http://localhost:9090/*"
   - Save

4. Generate client secret:
   - Go to the "Credentials" tab of the client
   - Copy the client secret and update it in your application.yaml file or set as environment variable KEYCLOAK_CLIENT_SECRET

5. Create roles:
   - Go to "Roles" → "Create Role"
   - Create roles named "USER" and "ADMIN"

6. Create a test user:
   - Go to "Users" → "Add User"
   - Fill in username, email, and other details
   - Set the password in the "Credentials" tab
   - Map roles in the "Role Mappings" tab

## 3. Running Tests and Generating Coverage Reports

The project uses JaCoCo for code coverage. Run the tests and generate coverage reports with:

```bash
# Run all tests
./mvnw test

# Generate JaCoCo report
./mvnw jacoco:report

# Run tests with coverage check (requires 80% coverage)
./mvnw verify
```

The JaCoCo report will be available at:
`target/site/jacoco/index.html`

## 4. Integration Tests

Integration tests are configured to run using:
- H2 in-memory database
- Mock Keycloak authentication

To run only the authentication integration tests:

```bash
./mvnw test -Dtest=**/auth/*Test.java
```

## 5. Security Endpoints

The authentication package provides the following endpoints:

- `GET /api/auth/userinfo` - Returns information about the authenticated user
- `GET /api/auth/status` - Simple status endpoint that requires authentication

Both endpoints require a valid JWT token from Keycloak.
