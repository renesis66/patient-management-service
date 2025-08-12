# Patient Management Service API

## Overview
A healthcare-focused REST API for managing patient demographics, identity, and medical record numbers with DynamoDB persistence, built with Micronaut and Kotlin following healthcare standards.

**Base URL:** `http://localhost:8082`

**API Base Path:** `/patients`

Complete base URL for all API calls:
```
http://localhost:8082/patients
```

### Example Endpoint URLs:
- **List all patients:** `http://localhost:8082/patients`
- **Get patient by ID:** `http://localhost:8082/patients/{id}`
- **Create patient:** `http://localhost:8082/patients`
- **Update patient:** `http://localhost:8082/patients/{id}`
- **Delete patient:** `http://localhost:8082/patients/{id}`

## Endpoints

### Patient Management
- **GET** `/patients`
  - List all patients
  - Requires: JWT Authentication

- **GET** `/patients/{id}`
  - Get patient by ID
  - Requires: JWT Authentication

- **POST** `/patients`
  - Create new patient
  - Body: Patient data with demographics
  - Requires: JWT Authentication

- **PUT** `/patients/{id}`
  - Update existing patient
  - Body: Updated patient data
  - Requires: JWT Authentication

- **DELETE** `/patients/{id}`
  - Delete patient (soft delete for audit trail)
  - Requires: JWT Authentication

## Data Models

### Patient
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "mrn": "MRN-2024-001",
  "firstName": "John",
  "lastName": "Doe",
  "dateOfBirth": "1985-03-15",
  "gender": "MALE",
  "phoneNumber": "+1-555-0123",
  "email": "john.doe@email.com",
  "address": {
    "street": "123 Main St",
    "city": "Anytown",
    "state": "CA",
    "zipCode": "12345",
    "country": "USA"
  },
  "emergencyContact": {
    "name": "Jane Doe",
    "relationship": "SPOUSE",
    "phoneNumber": "+1-555-0124"
  },
  "createdAt": "2024-01-15T08:00:00Z",
  "updatedAt": "2024-01-15T08:00:00Z"
}
```

## Features
- ✅ Complete CRUD operations for patient management
- ✅ Medical Record Number (MRN) uniqueness enforcement
- ✅ JWT authentication for all endpoints
- ✅ HIPAA-compliant data handling
- ✅ Single-table DynamoDB design with GSI for MRN lookup
- ✅ In-memory repository for development/testing
- ✅ Comprehensive input validation
- ✅ Audit trail with timestamps

## Getting Started

### Prerequisites
- Java 17+
- Docker or Rancher Desktop (optional, for DynamoDB Local)

### Quick Start
```bash
# Test the service (no external dependencies)
./gradlew test

# Start the service
./gradlew run
```

### Manual Setup

#### Step 1: Start Docker/Rancher Desktop (Optional)
If using DynamoDB Local:
```bash
open -a "Rancher Desktop"
# Wait for Rancher Desktop to fully start (may take 30-60 seconds)
```

#### Step 2: Start the Service
```bash
# Check if port 8080 is in use
lsof -i :8080

# Kill existing process if needed
kill <PID>

# Start on default port 8080
./gradlew run

# Or start on custom port
MICRONAUT_SERVER_PORT=8082 ./gradlew run
```

#### Step 3: Verify Service is Running
```bash
# Test endpoint (should return "Unauthorized" - this is expected)
curl -s http://localhost:8082/patients
```

### DynamoDB Local (Optional)
The service runs with in-memory repository by default. For DynamoDB Local:

```bash
# Navigate to DynamoDB Local directory
cd dynamodb-local/

# Start DynamoDB Local
java -Djava.library.path=./DynamoDBLocal_lib -jar DynamoDBLocal.jar -sharedDb -port 8000

# Create patients table
aws dynamodb create-table \
    --table-name patients \
    --attribute-definitions \
        AttributeName=PK,AttributeType=S \
        AttributeName=SK,AttributeType=S \
        AttributeName=GSI1PK,AttributeType=S \
        AttributeName=GSI1SK,AttributeType=S \
    --key-schema \
        AttributeName=PK,KeyType=HASH \
        AttributeName=SK,KeyType=RANGE \
    --global-secondary-indexes \
        "IndexName=GSI1,KeySchema=[{AttributeName=GSI1PK,KeyType=HASH},{AttributeName=GSI1SK,KeyType=RANGE}],Projection={ProjectionType=ALL},ProvisionedThroughput={ReadCapacityUnits=5,WriteCapacityUnits=5}" \
    --provisioned-throughput \
        ReadCapacityUnits=5,WriteCapacityUnits=5 \
    --endpoint-url http://localhost:8000 \
    --region us-east-1
```

### Run Tests
```bash
./gradlew test
```

### Build
```bash
./gradlew build
```

### Build Shadow JAR
```bash
./gradlew shadowJar
```

### Troubleshooting

1. **Port already in use:**
   ```bash
   lsof -i :8080
   kill <PID>
   # Or use: MICRONAUT_SERVER_PORT=8082 ./gradlew run
   ```

2. **Authentication required:**
   - All endpoints require JWT authentication
   - Use appropriate authentication headers in requests

3. **DynamoDB connection issues:**
   - Service defaults to in-memory repository
   - Only use DynamoDB Local if specifically needed

The service will start on the configured port with JWT authentication enabled for all patient data operations.