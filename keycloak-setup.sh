#!/bin/bash

# Script to auto-configure the Keycloak realm for the FinGest application
# This script requires the jq and curl tools to be installed

echo "Starting Keycloak Setup..."

# Wait for Keycloak to be ready
echo "Waiting for Keycloak to start..."
while ! curl -s http://localhost:8080 > /dev/null; do
  sleep 5
  echo "Still waiting..."
done

echo "Keycloak is up. Starting configuration..."

# Get admin token
echo "Getting admin token..."
ADMIN_TOKEN=$(curl -s -X POST http://localhost:8080/realms/master/protocol/openid-connect/token \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d 'username=admin' \
  -d 'password=admin' \
  -d 'grant_type=password' \
  -d 'client_id=admin-cli' | jq -r '.access_token')

if [ -z "$ADMIN_TOKEN" ] || [ "$ADMIN_TOKEN" == "null" ]; then
  echo "Failed to get admin token. Exiting."
  exit 1
fi

echo "Admin token obtained successfully."

# Create realm
echo "Creating fingest-realm..."
curl -s -X POST http://localhost:8080/admin/realms \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{
    "realm": "fingest-realm",
    "enabled": true,
    "accessTokenLifespan": 300,
    "ssoSessionIdleTimeout": 1800,
    "ssoSessionMaxLifespan": 36000,
    "refreshTokenMaxReuse": 0,
    "accessCodeLifespan": 60
  }'

echo "Creating client..."
# Create client
CLIENT_ID=$(curl -s -X POST http://localhost:8080/admin/realms/fingest-realm/clients \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{
    "clientId": "fingest-client",
    "enabled": true,
    "protocol": "openid-connect",
    "redirectUris": ["http://localhost:9090/*"],
    "publicClient": false,
    "standardFlowEnabled": true,
    "directAccessGrantsEnabled": true,
    "serviceAccountsEnabled": true,
    "authorizationServicesEnabled": false,
    "clientAuthenticatorType": "client-secret"
  }' | jq -r '.id')

echo "Getting client secret..."
# Get the client secret
CLIENT_SECRET=$(curl -s -X GET http://localhost:8080/admin/realms/fingest-realm/clients/${CLIENT_ID}/client-secret \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H 'Content-Type: application/json' | jq -r '.value')

echo "Creating roles..."
# Create USER role
curl -s -X POST http://localhost:8080/admin/realms/fingest-realm/roles \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{
    "name": "USER"
  }'

# Create ADMIN role
curl -s -X POST http://localhost:8080/admin/realms/fingest-realm/roles \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{
    "name": "ADMIN"
  }'

echo "Creating test users..."
# Create test user with USER role
curl -s -X POST http://localhost:8080/admin/realms/fingest-realm/users \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "firstName": "Test",
    "lastName": "User",
    "enabled": true,
    "credentials": [{"type": "password", "value": "password", "temporary": false}]
  }'

USER_ID=$(curl -s -X GET http://localhost:8080/admin/realms/fingest-realm/users\?username=testuser \
  -H "Authorization: Bearer $ADMIN_TOKEN" | jq -r '.[0].id')

USER_ROLE_ID=$(curl -s -X GET http://localhost:8080/admin/realms/fingest-realm/roles/USER \
  -H "Authorization: Bearer $ADMIN_TOKEN" | jq -r '.id')

# Assign USER role to testuser
curl -s -X POST http://localhost:8080/admin/realms/fingest-realm/users/${USER_ID}/role-mappings/realm \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H 'Content-Type: application/json' \
  -d '[{
    "id": "'"${USER_ROLE_ID}"'",
    "name": "USER"
  }]'

# Create admin user
curl -s -X POST http://localhost:8080/admin/realms/fingest-realm/users \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{
    "username": "admin",
    "email": "admin@example.com",
    "firstName": "Admin",
    "lastName": "User",
    "enabled": true,
    "credentials": [{"type": "password", "value": "admin123", "temporary": false}]
  }'

ADMIN_USER_ID=$(curl -s -X GET http://localhost:8080/admin/realms/fingest-realm/users\?username=admin \
  -H "Authorization: Bearer $ADMIN_TOKEN" | jq -r '.[0].id')

ADMIN_ROLE_ID=$(curl -s -X GET http://localhost:8080/admin/realms/fingest-realm/roles/ADMIN \
  -H "Authorization: Bearer $ADMIN_TOKEN" | jq -r '.id')

# Assign ADMIN role to admin user
curl -s -X POST http://localhost:8080/admin/realms/fingest-realm/users/${ADMIN_USER_ID}/role-mappings/realm \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H 'Content-Type: application/json' \
  -d '[{
    "id": "'"${ADMIN_ROLE_ID}"'",
    "name": "ADMIN"
  }]'

echo "Setup complete!"
echo "Client Secret: ${CLIENT_SECRET}"
echo "Add the client secret to your application.yaml or set as KEYCLOAK_CLIENT_SECRET environment variable"
