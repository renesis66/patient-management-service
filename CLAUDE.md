# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Patient Management Service built with Micronaut and Kotlin, using DynamoDB for data storage. Focused on patient identity, demographics, and medical record number management.

## Technology Stack

- **Framework**: Micronaut 4.2.1
- **Language**: Kotlin 1.9.22
- **Database**: AWS DynamoDB (single-table design)
- **Build Tool**: Gradle with Kotlin DSL
- **Authentication**: JWT with Micronaut Security

## Common Commands

```bash
# Build the application
./gradlew build

# Run the application
./gradlew run

# Run tests
./gradlew test

# Run application with specific profile
./gradlew run --args="-Dmicronaut.environments=prod"

# Build shadow JAR
./gradlew shadowJar

# Clean build
./gradlew clean
```

## Starting Services

### Service Startup Commands
To start the patient-management-service and its dependencies:

#### 1. Start Docker/Rancher Desktop (if using Rancher Desktop)
```bash
open -a "Rancher Desktop"
# Wait for Rancher Desktop to fully initialize (30-60 seconds)
# Check if ready: docker ps
```

#### 2. Test the Service (No External Dependencies Required)
```bash
# Run tests to verify service works
./gradlew test
```

#### 3. Start the Service
```bash
# Start on default port 8080
./gradlew run

# Start on custom port (if 8080 is in use)
MICRONAUT_SERVER_PORT=8082 ./gradlew run

# Check if port is in use first
lsof -i :8080
kill <PID>  # If needed
```

#### 4. Verify Service is Running
```bash
# Test endpoint (should return Unauthorized - this is expected)
curl -s http://localhost:8082/patients
```

### DynamoDB Local (Optional)
The service can run with in-memory repository for development. If you need DynamoDB Local:

```bash
# Start DynamoDB Local (run from DynamoDB Local directory)
cd dynamodb-local/
java -Djava.library.path=./DynamoDBLocal_lib -jar DynamoDBLocal.jar -sharedDb -port 8000

# Or start with different port if 8000 is in use
java -Djava.library.path=./DynamoDBLocal_lib -jar DynamoDBLocal.jar -sharedDb -port 8001
```

### Alternative JAR Startup (After Build)
```bash
# Build the JAR first
./gradlew shadowJar

# Start with in-memory repository (for testing/development)
java -Dmicronaut.environments=test -jar build/libs/patient-management-service-0.1-all.jar

# Start with DynamoDB (requires DynamoDB Local to be running)
java -Dmicronaut.environments=dynamodb -jar build/libs/patient-management-service-0.1-all.jar

# Run in background
java -Dmicronaut.environments=test -jar build/libs/patient-management-service-0.1-all.jar > service.log 2>&1 &
```

### Troubleshooting Startup Issues

1. **Port 8080 already in use:**
   ```bash
   lsof -i :8080
   kill <PID>
   # Or use custom port: MICRONAUT_SERVER_PORT=8082 ./gradlew run
   ```

2. **Docker/DynamoDB issues:**
   ```bash
   docker context ls
   docker context use desktop-linux  # For Rancher Desktop
   ```

3. **Service fails to start:**
   ```bash
   # Check logs for detailed error messages
   ./gradlew run --debug
   ```

Service will be available at: **http://localhost:8082** (or configured port)

## DynamoDB Table Design

Single table: `patients`

**Access Patterns:**
- Get patient by ID: PK = `PATIENT#{id}`, SK = `METADATA`
- Find patient by MRN: GSI1PK = `MRN#{mrn}`, GSI1SK = `PATIENT`

**Indexes:**
- Primary: PK, SK
- GSI1: GSI1PK, GSI1SK

## API Endpoints

Patient Identity & Demographics:
- `GET /patients` - List all patients
- `GET /patients/{id}` - Get patient by ID
- `POST /patients` - Create new patient
- `PUT /patients/{id}` - Update patient
- `DELETE /patients/{id}` - Delete patient

## Domain Focus

- Patient registration and demographics
- Medical record number (MRN) management
- Patient identity verification
- HIPAA compliant data handling

## Development Notes

- All endpoints require authentication (JWT)
- Patient data uses single-table DynamoDB design
- Medical Record Numbers must be unique (enforced via GSI1)
- Always update `updatedAt` timestamp on modifications
- Use proper HTTP status codes for medical data operations